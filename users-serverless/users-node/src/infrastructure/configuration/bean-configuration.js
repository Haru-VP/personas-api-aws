import { DynamoDBClient } from '@aws-sdk/client-dynamodb';
import { DynamoDBDocumentClient } from '@aws-sdk/lib-dynamodb';
import { UserHandler } from '../../application/handler/user-handler.js';
import { UserUseCase } from '../../domain/usecase/user-use-case.js';
import { HttpRequestReader } from '../entrypoint/http/http-request-reader.js';
import { HttpResponseFactory } from '../entrypoint/http/http-response-factory.js';
import { ApiExceptionHandler } from '../exceptionhandler/api-exception-handler.js';
import { DynamoUserAdapter } from '../persistence/adapter/dynamo-user-adapter.js';

const USERS_TABLE_VARIABLE = 'USERS_TABLE';
const USERS_EMAIL_INDEX_VARIABLE = 'USERS_EMAIL_INDEX';

export const requiredVariable = (environment, name) => {
  const value = environment[name];
  if (typeof value !== 'string' || value.trim() === '') {
    throw new Error(`Missing environment variable ${name}`);
  }
  return value;
};

const documentClient = DynamoDBDocumentClient.from(new DynamoDBClient({}));
const responseFactory = new HttpResponseFactory();

export const beans = Object.freeze({
  userHandler: new UserHandler(
    new UserUseCase(
      new DynamoUserAdapter(
        documentClient,
        requiredVariable(process.env, USERS_TABLE_VARIABLE),
        requiredVariable(process.env, USERS_EMAIL_INDEX_VARIABLE),
      ),
    ),
  ),
  requestReader: new HttpRequestReader(),
  responseFactory,
  exceptionHandler: new ApiExceptionHandler(responseFactory),
});
