import { Record } from 'immutable'

const ExperimentVariantRecord = Record({
  name: '',
  color: '',
  predicatesInfo: '',
  deviceClass: ''
})

export default class ExperimentVariantModel extends ExperimentVariantRecord {
  constructor (isDraft, variant, i) {
    variant = Object.assign({}, variant)
    variant.predicatesInfo = ExperimentVariantModel.predicatesInfo(isDraft, variant)
    variant.color = ExperimentVariantModel.color(variant.name, i)
    variant.deviceClass = ExperimentVariantModel.deviceClass(variant)
    super(variant)
  }

  isBase () {
    return this.name === 'base'
  }

  static color (name, i) {
    if (name === 'base') {
      return 'grey'
    }
    let colors = ['orange', 'cyan', 'yellow', 'green', 'pink', 'blue', 'amber', 'lime', 'red']
    return colors[i % colors.length]
  }

  static predicatesInfo (isDraft, variant) {
    return variant.predicates.map(it =>
      ExperimentVariantModel.predicateToString(isDraft, it)).join(' AND ')
  }

  static deviceClass (variant) {
    const devicePredicate = variant.predicates.find(p => p.type === 'DEVICE_CLASS')

    if (devicePredicate) {
      return devicePredicate.device
    }
  }

  static predicateToString (isDraft, p) {
    if (p.type === 'INTERNAL') {
      return 'INTERNAL'
    } else if (p.type === 'HASH') {
      return `${(p.to - p.from)} %`
    } else if (p.type === 'DEVICE_CLASS') {
      return p.device
    } else if (p.type === 'CMUID_REGEXP') {
      return p.regexp
    } else if (p.type === 'CUSTOM_PARAM') {
      return p.name + ' = ' + p.value
    } else if (p.type === 'SHRED_HASH' && !isDraft) {
      return p.ranges.map(r => `${r.from} - ${r.to} %`).join(' AND ')
    } else if (p.type === 'SHRED_HASH' && isDraft) {
      return p.ranges.map(r => `${(r.to - r.from)} %`).join(' AND ')
    }
    return ''
  }
}
