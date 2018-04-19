export function startsOrEndsWithSpace (str) {
  if (str === null || str === undefined) {
    return false
  }
  return str.startsWith(' ') || str.endsWith(' ')
}
