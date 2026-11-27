import assert from 'node:assert/strict';
import { describe, it } from 'node:test';
import { beans, requiredVariable } from '../../../src/infrastructure/configuration/bean-configuration.js';

describe('bean configuration', () => {
  it('builds every bean from the environment', () => {
    assert.ok(beans.userHandler);
    assert.ok(beans.requestReader);
    assert.ok(beans.responseFactory);
    assert.ok(beans.exceptionHandler);
  });

  it('requiredVariable returns the value when present', () => {
    assert.equal(requiredVariable({ USERS_TABLE: 'users-dev' }, 'USERS_TABLE'), 'users-dev');
  });

  for (const value of [undefined, '', '   ']) {
    it(`requiredVariable fails fast when the value is ${JSON.stringify(value)}`, () => {
      assert.throws(() => requiredVariable({ USERS_TABLE: value }, 'USERS_TABLE'), {
        message: 'Missing environment variable USERS_TABLE',
      });
    });
  }
});
