import { Record } from 'immutable'

const ExperimentVariantRecord = Record({
  name: ''
})

export default class ExperimentVariantModel extends ExperimentVariantRecord {
  constructor (experimentVariantObject) {
    experimentVariantObject = Object.assign({}, experimentVariantObject)

    super(experimentVariantObject)
  }
}
