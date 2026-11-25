import { UserHandler } from '../../application/handler/user-handler.js';
import { UserUseCase } from '../../domain/usecase/user-use-case.js';
import { HttpRequestReader } from '../entrypoint/http/http-request-reader.js';
import { HttpResponseFactory } from '../entrypoint/http/http-response-factory.js';
import { ApiExceptionHandler } from '../exceptionhandler/api-exception-handler.js';
import { InMemoryUserAdapter } from '../persistence/adapter/in-memory-user-adapter.js';

const responseFactory = new HttpResponseFactory();

export const beans = Object.freeze({
  userHandler: new UserHandler(new UserUseCase(new InMemoryUserAdapter())),
  requestReader: new HttpRequestReader(),
  responseFactory,
  exceptionHandler: new ApiExceptionHandler(responseFactory),
});
