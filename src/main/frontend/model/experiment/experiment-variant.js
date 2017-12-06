import { Record } from 'immutable';

const ExperimentVariantRecord = Record({
  name: ''
});

export default class ExperimentVariantModel extends ExperimentVariantRecord {
  constructor(object) {
    object = Object.assign({}, object);

    super(object);
  }
};
