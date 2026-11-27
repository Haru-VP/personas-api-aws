import assert from 'node:assert/strict';
import { beforeEach, describe, it, mock } from 'node:test';
import { DeleteCommand, GetCommand, PutCommand, QueryCommand } from '@aws-sdk/lib-dynamodb';
import { User } from '../../../../src/domain/model/user.js';
import { DynamoUserAdapter } from '../../../../src/infrastructure/persistence/adapter/dynamo-user-adapter.js';

const TABLE = 'users-test';
const INDEX = 'email-index';
const USER = new User({ id: '1', name: 'Ana Gómez', email: 'ana@mail.com' });

describe('DynamoUserAdapter', () => {
  let documentClient;
  let adapter;

  const sentCommand = () => documentClient.send.mock.calls[0].arguments[0];

  beforeEach(() => {
    documentClient = { send: mock.fn(async () => ({})) };
    adapter = new DynamoUserAdapter(documentClient, TABLE, INDEX);
  });

  it('saveUser puts the item with english attributes', async () => {
    assert.equal(await adapter.saveUser(USER), USER);

    assert.ok(sentCommand() instanceof PutCommand);
    assert.deepEqual(sentCommand().input, {
      TableName: TABLE,
      Item: { id: '1', name: 'Ana Gómez', email: 'ana@mail.com' },
    });
  });

  it('findById returns the user using a consistent read', async () => {
    documentClient.send.mock.mockImplementation(async () => ({
      Item: { id: '1', name: 'Ana Gómez', email: 'ana@mail.com' },
    }));

    assert.deepEqual(await adapter.findById('1'), USER);
    assert.ok(sentCommand() instanceof GetCommand);
    assert.deepEqual(sentCommand().input, { TableName: TABLE, Key: { id: '1' }, ConsistentRead: true });
  });

  it('findById returns null when the item does not exist', async () => {
    assert.equal(await adapter.findById('999'), null);
  });

  it('existsByEmail queries the email index with limit 1', async () => {
    documentClient.send.mock.mockImplementation(async () => ({ Count: 1 }));

    assert.equal(await adapter.existsByEmail('ana@mail.com'), true);
    assert.ok(sentCommand() instanceof QueryCommand);
    assert.deepEqual(sentCommand().input, {
      TableName: TABLE,
      IndexName: INDEX,
      KeyConditionExpression: 'email = :email',
      ExpressionAttributeValues: { ':email': 'ana@mail.com' },
      Limit: 1,
    });
  });

  it('existsByEmail returns false when the index has no match', async () => {
    documentClient.send.mock.mockImplementation(async () => ({ Count: 0 }));

    assert.equal(await adapter.existsByEmail('nobody@mail.com'), false);
  });

  it('deleteById deletes the item by key', async () => {
    await adapter.deleteById('1');

    assert.ok(sentCommand() instanceof DeleteCommand);
    assert.deepEqual(sentCommand().input, { TableName: TABLE, Key: { id: '1' } });
  });
});
