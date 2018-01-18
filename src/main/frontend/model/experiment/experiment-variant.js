import { Record } from 'immutable'

const ExperimentVariantRecord = Record({
  name: '',
  color: '',
  predicatesInfo: ''
})

export default class ExperimentVariantModel extends ExperimentVariantRecord {
  constructor (variant, i) {
    variant = Object.assign({}, variant)
    variant.predicatesInfo = ExperimentVariantModel.predicatesInfo(variant)
    variant.color = ExperimentVariantModel.color(variant.name, i)
    super(variant)
  }

  static color (name, i) {
    if (name === 'base') {
      return 'gray'
    }
    let colors = ['orange', 'cyan', 'yellow', 'green', 'pink', 'blue', 'amber', 'lime', 'red']
    return colors[i % colors.length]
  }

  static predicatesInfo (variant) {
    return variant.predicates.map(ExperimentVariantModel.predicateToString).join(', ')
  }

  static predicateToString (p) {
    if (p.type === 'INTERNAL') {
      return 'INTERNAL'
    } else if (p.type === 'HASH') {
      return `${(p.to - p.from)} %`
    } else if (p.type === 'DEVICE_CLASS') {
      return p.device
    } else if (p.type === 'CMUID_REGEXP') {
      return p.regexp
    }
    return ''
  }
}
