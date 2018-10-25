export function formatError (error) {
  if (error.response &&
      error.response.status &&
      error.response.statusText &&
      error.response.data &&
      error.response.data.errors &&
      error.response.data.errors[0]) {
    const firstError = error.response.data.errors[0]

    const message = 'Error: ' + firstError.message +
      '. Code: ' + firstError.code +
      '. Status: ' + error.response.status + ' ' + error.response.statusText

    console.error('error.response', error.response)
    return message
  }

  if (error.response &&
      error.response.status &&
      error.response.statusText) {
    const message = 'Error! Response status: ' + error.response.status + ' ' + error.response.statusText

    console.error('error.response', error.response)
    return message
  }

  console.error('error', error)
  return error
}
