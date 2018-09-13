import { Record } from 'immutable'

const ExperimentGroupRecord = Record({
  id: null,
  experimentIds: []
})

export default class ExperimentGroup extends ExperimentGroupRecord {
  constructor (experimentGroupObject) {
    super({
      id: experimentGroupObject.id,
      experimentIds: experimentGroupObject.experiments
    })
  }

  size () {
    return this.experimentIds.length
  }
}
