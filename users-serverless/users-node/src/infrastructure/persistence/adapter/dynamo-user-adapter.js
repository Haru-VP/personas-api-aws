import { DeleteCommand, GetCommand, PutCommand, QueryCommand } from '@aws-sdk/lib-dynamodb';
import { User } from '../../../domain/model/user.js';
import { UserPersistencePort } from '../../../domain/repositoryport/user-persistence-port.js';

const EMAIL_CONDITION = 'email = :email';

export class DynamoUserAdapter extends UserPersistencePort {
  #documentClient;
  #tableName;
  #emailIndexName;

  constructor(documentClient, tableName, emailIndexName) {
    super();
    this.#documentClient = documentClient;
    this.#tableName = tableName;
    this.#emailIndexName = emailIndexName;
  }

  async saveUser(user) {
    await this.#documentClient.send(
      new PutCommand({
        TableName: this.#tableName,
        Item: { id: user.id, name: user.name, email: user.email },
      }),
    );
    return user;
  }

  async findById(id) {
    const { Item } = await this.#documentClient.send(
      new GetCommand({ TableName: this.#tableName, Key: { id }, ConsistentRead: true }),
    );
    return Item ? new User(Item) : null;
  }

  async existsByEmail(email) {
    const { Count } = await this.#documentClient.send(
      new QueryCommand({
        TableName: this.#tableName,
        IndexName: this.#emailIndexName,
        KeyConditionExpression: EMAIL_CONDITION,
        ExpressionAttributeValues: { ':email': email },
        Limit: 1,
      }),
    );
    return Count > 0;
  }

  async deleteById(id) {
    await this.#documentClient.send(new DeleteCommand({ TableName: this.#tableName, Key: { id } }));
  }
}
