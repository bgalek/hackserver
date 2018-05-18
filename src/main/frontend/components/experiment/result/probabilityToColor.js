export function probabilityToColor (probability, color) {
  const steps = [0.9, 0.75, 0.50, 0.25]
  const gray = '#eeeeee'
  const green = ['#00b300', '#6fc06f', '#b6dfb6', '#d7edd8']
  const red = ['#e62e00', '#ef8779', '#f9cac1', '#ecd3cc']
  const index = steps.findIndex(x => probability >= x)
  if (index === -1) {
    return gray
  }
  if (color === 'red') {
    return red[index]
  } else if (color === 'green') {
    return green[index]
  }
  return gray
}
