export default function (error) {
  var msg = error &&
    error.response &&
    error.response.data &&
    error.response.data.errors &&
    error.response.data.errors[0] &&
    error.response.data.errors[0].message

  return msg === undefined ? error : Error(msg)
}
