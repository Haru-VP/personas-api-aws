import { beans } from '../configuration/bean-configuration.js';
import { createApiGatewayFunction } from './api-gateway-function.js';

const ID_PATH_PARAMETER = 'id';

export const buildDeleteUserFunction = ({ userHandler, requestReader, responseFactory, exceptionHandler }) =>
  createApiGatewayFunction(async (event) => {
    await userHandler.deleteUser(requestReader.readPathParameter(event, ID_PATH_PARAMETER));
    return responseFactory.noContent();
  }, exceptionHandler);

export const handler = buildDeleteUserFunction(beans);
