
export function variantColor(i) {
  let colors = ['orange', 'cyan', 'yellow', 'green', 'pink', 'blue', 'amber', 'lime']
  if (i >= colors.length) {
    return 'grey'
  }
  return colors[i]
}
