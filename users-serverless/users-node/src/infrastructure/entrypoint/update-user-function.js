import { beans } from '../configuration/bean-configuration.js';
import { createApiGatewayFunction } from './api-gateway-function.js';

const ID_PATH_PARAMETER = 'id';

export const buildUpdateUserFunction = ({ userHandler, requestReader, responseFactory, exceptionHandler }) =>
  createApiGatewayFunction(async (event) => {
    const id = requestReader.readPathParameter(event, ID_PATH_PARAMETER);
    const request = requestReader.readBody(event);
    return responseFactory.ok(await userHandler.updateUser(id, request));
  }, exceptionHandler);

export const handler = buildUpdateUserFunction(beans);
