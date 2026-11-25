export const createApiGatewayFunction = (process, exceptionHandler) => async (event) => {
  try {
    return await process(event);
  } catch (error) {
    return exceptionHandler.toResponse(error);
  }
};
