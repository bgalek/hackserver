import { Record, List } from 'immutable';
import moment from 'moment'

import ExperimentVariantModel from './experiment-variant';

const ExperimentRecord = Record({
  id: null,
  variants: [],
  description: '',
  owner: '',
  activeFrom: null,
  activeTo: null,
  reportingEnabled: true
});

const DEFAULT_FORMAT = 'MMMM Do YYYY, hh:mm:ss';

export default class ExperimentModel extends ExperimentRecord {
  constructor(object) {
    object = Object.assign({}, object);
    object.activeFrom = object.activeFrom ? new Date(object.activeFrom) : null;
    object.activeTo = object.activeTo ? new Date(object.activeTo) : null;
    object.variants = List(object.variants).map(variant => new ExperimentVariantModel(variant)).toArray();

    super(object);
  }

  fromDateString() {
    return this.activeFrom && moment(this.activeFrom).format(DEFAULT_FORMAT)
  }

  toDateString() {
    return this.activeTo && moment(this.activeTo).format(DEFAULT_FORMAT)
  }
};
