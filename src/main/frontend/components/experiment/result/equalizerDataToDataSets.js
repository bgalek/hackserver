import { probabilityToColor } from './probabilityToColor'

export function equalizerDataToDataSets (equalizerData, RADIUS) {
  const labels = equalizerData.bars.map(v => v.variantName)
  const pluses = labels.map(() => 1)
  const minuses = labels.map(() => -1)

  let datasets = new Array(RADIUS).fill({
    label: '',
    backgroundColor: new Array(labels.length).fill('#eeeeee'),
    hoverBackgroundColor: new Array(labels.length).fill('#eeeeee'),
    borderColor: '#000000',
    borderWidth: 1,
    data: minuses
  }).concat(new Array(RADIUS).fill({
    label: '',
    backgroundColor: new Array(labels.length).fill('#eeeeee'),
    hoverBackgroundColor: new Array(labels.length).fill('#eeeeee'),
    borderColor: '#000000',
    borderWidth: 1,
    data: pluses
  })).map(x => Object.assign({}, x))

  equalizerData.bars.forEach((v, vidx) => {
    v.improvingProbabilities.forEach((diff, idx) => {
      const i = RADIUS + idx
      datasets[i].backgroundColor = datasets[i].backgroundColor.slice(0)
      datasets[i].backgroundColor[vidx] = probabilityToColor(diff, 'green')
      datasets[i].hoverBackgroundColor = datasets[i].backgroundColor
    })
    v.worseningProbabilities.forEach((diff, idx) => {
      const i = idx
      datasets[i].backgroundColor = datasets[i].backgroundColor.slice(0)
      datasets[i].backgroundColor[vidx] = probabilityToColor(diff, 'red')
      datasets[i].hoverBackgroundColor = datasets[i].backgroundColor
    })
  })

  return {
    labels,
    datasets
  }
}
