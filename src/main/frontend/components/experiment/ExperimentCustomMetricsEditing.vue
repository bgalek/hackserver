<template>
  <div>
    <v-layout row v-if="showHeader">
        <h3 class="mt-3 blue--text">Custom metrics definition</h3>
    </v-layout>
      <v-switch
        label="Define custom metric"
        v-model="defineCustomMetric"
        v-on:change="customMetricChange"
        v-if="!readOnly"
      ></v-switch>
      <v-btn color="primary" class="mb-2" @click="newItem()" v-if="notEveryVariantHaveCustomMetric()">
        Add custom metric
      </v-btn>
    <v-dialog v-model="editing" max-width="450px">
      <v-form v-model="customMetricDefinitionValid" ref="customMetricDefinitionForm">
        <v-card>
          <v-card-title>
            <span class="headline">Custom metric name</span>
          </v-card-title>
          <v-layout fluid class="pa-3 ma-0">
            <v-flex>
              <v-text-field label="name" v-model="editedItem.metricName"
                            :rules="customMetricNameRules"
                            :disabled="isMetricNameSet()"

              ></v-text-field>
            </v-flex>
          </v-layout>
          <v-layout fluid class="pa-3 ma-0">
            <v-flex>
              <v-select label="variant" v-model="editedItem.definitionForVariants.variantName"
                            :items="getVariants()"
                            :disabled="isMetricAssignedToAllVariants"
                            :rules="variantRules"
              ></v-select>
            </v-flex>
          </v-layout>
          <v-layout fluid class="pa-3 ma-0">
            <v-flex>
              <v-checkbox
                label="Assign to all variants"
                v-model="isMetricAssignedToAllVariants"
                :disabled="haveOnlyBaseVariant() || this.items.length > 0"
              ></v-checkbox>
            </v-flex>
          </v-layout>
          <v-layout>
            <v-flex xs11 lg6>

              <v-card-title>
                <span class="headline">View event definition</span>
              </v-card-title>
              <v-card-text>

                <v-container fluid class="pa-0 ma-0">

                  <v-layout row align-center>
                    <v-flex xs2>
                      <v-tooltip top close-delay="3000">
                        For example: <code>offers_reco</code>
                        <v-icon slot="activator">help_outline</v-icon>
                      </v-tooltip>
                    </v-flex>


                    <v-flex>
                      <v-text-field label="boxName" v-model="editedItem.definitionForVariants.viewEventDefinition.boxName"
                                    :rules="filterRules"
                      ></v-text-field>
                    </v-flex>
                  </v-layout>

                  <v-layout row align-center>
                    <v-flex offset-xs2>
                      <v-text-field label="category" v-model="editedItem.definitionForVariants.viewEventDefinition.category"
                                    :rules="filterRules"
                      ></v-text-field>
                    </v-flex>
                  </v-layout>

                  <v-layout row align-center>
                    <v-flex offset-xs2>
                      <v-text-field label="label" v-model="editedItem.definitionForVariants.viewEventDefinition.label"
                                    :rules="filterRules"
                      ></v-text-field>
                    </v-flex>
                  </v-layout>

                  <v-layout row align-center>
                    <v-flex xs2>
                      <v-tooltip top close-delay="3000">
                        For example: <br/>
                        <code>itemView</code><br/>
                        <code>boxView</code><br/>
                        <code>suggestionsShow</code><br/>
                        <code>click</code><br/>
                        <code>boxInteraction</code>
                        <v-icon slot="activator">help_outline</v-icon>
                      </v-tooltip>
                    </v-flex>
                    <v-flex>
                      <v-text-field label="action" v-model="editedItem.definitionForVariants.viewEventDefinition.action"
                                    :rules="filterRules"
                      ></v-text-field>
                    </v-flex>
                  </v-layout>

                  <v-layout row align-center>
                    <v-flex offset-xs2>
                      <v-text-field label="value" v-model="editedItem.definitionForVariants.viewEventDefinition.value"
                                    :rules="filterRules"
                      ></v-text-field>
                    </v-flex>
                  </v-layout>
                  <v-layout row align-center>
                    <v-flex offset-xs2>
                      <div class="error--text" v-if="error.length">
                        {{error}}
                      </div>
                    </v-flex>
                  </v-layout>
                </v-container>

              </v-card-text>
            </v-flex>
            <v-flex xs11 lg6>

              <v-card-title>
                <span class="headline">Success event definition</span>
              </v-card-title>
              <v-card-text>

                <v-container fluid class="pa-0 ma-0">

                  <v-layout row align-center>
                    <v-flex xs2>
                      <v-tooltip top close-delay="3000">
                        For example: <code>offers_reco</code>
                        <v-icon slot="activator">help_outline</v-icon>
                      </v-tooltip>
                    </v-flex>
                    <v-flex>
                      <v-text-field label="boxName" v-model="editedItem.definitionForVariants.successEventDefinition.boxName"
                                    :rules="filterRules"
                      ></v-text-field>
                    </v-flex>
                  </v-layout>

                  <v-layout row align-center>
                    <v-flex offset-xs2>
                      <v-text-field label="category" v-model="editedItem.definitionForVariants.successEventDefinition.category"
                                    :rules="filterRules"
                      ></v-text-field>
                    </v-flex>
                  </v-layout>

                  <v-layout row align-center>
                    <v-flex offset-xs2>
                      <v-text-field label="label" v-model="editedItem.definitionForVariants.successEventDefinition.label"
                                    :rules="filterRules"
                      ></v-text-field>
                    </v-flex>
                  </v-layout>

                  <v-layout row align-center>
                    <v-flex xs2>
                      <v-tooltip top close-delay="3000">
                        For example: <br/>
                        <code>itemView</code><br/>
                        <code>boxView</code><br/>
                        <code>suggestionsShow</code><br/>
                        <code>click</code><br/>
                        <code>boxInteraction</code>
                        <v-icon slot="activator">help_outline</v-icon>
                      </v-tooltip>
                    </v-flex>
                    <v-flex>
                      <v-text-field label="action" v-model="editedItem.definitionForVariants.successEventDefinition.action"
                                    :rules="filterRules"
                      ></v-text-field>
                    </v-flex>
                  </v-layout>

                  <v-layout row align-center>
                    <v-flex offset-xs2>
                      <v-text-field label="value" v-model="editedItem.definitionForVariants.successEventDefinition.value"
                                    :rules="filterRules"
                      ></v-text-field>
                    </v-flex>
                  </v-layout>
                </v-container>
              </v-card-text>
            </v-flex>
          </v-layout>
          <v-card-actions>
            <v-spacer></v-spacer>
            <v-container fluid class="pa-0 ma-0">

            <v-btn flat @click="close()">Cancel</v-btn>
            <v-btn color="gray"
                   :disabled="!changed()"
                   @click="saveEditing()"
                   style="text-transform: none">Save
            </v-btn>

            </v-container>
          </v-card-actions>
        </v-card>
      </v-form>
    </v-dialog>

    <v-data-table
        v-if="defineCustomMetric || readOnly"
        :headers="headers"
        :items="getItemsForTable()"
        hide-actions
        light
      >

        <template slot="items" slot-scope="props">
          <td>{{ props.item.type }}</td>
          <td>{{ props.item.definitionForVariants.variantName }}</td>
          <td>{{ props.item.boxName }}</td>
          <td>{{ props.item.category }}</td>
          <td>{{ props.item.label }}</td>
          <td>{{ props.item.action }}</td>
          <td>{{ props.item.value }}</td>

          <td class="justify-center layout px-0" v-if="!readOnly">
            <v-btn icon class="mx-0" @click="editItem(props.item)">
              <v-icon color="teal">edit</v-icon>
            </v-btn>
          </td>

        </template>
      </v-data-table>
    <div class="error--text" v-if="defineCustomMetric && !haveEveryVariantMetric()">
      You have to define custom metric for every variant.
    </div>
    <div v-if="metricName" class="mt-3">
      Metric name: {{metricName}}
    </div>
  </div>
</template>

<script>
  import {startsOrEndsWithSpace} from '../../utils/startsOrEndsWithSpace'
  import { Record, List } from 'immutable'
  import {containsNoSpecialCharacters} from '../../utils/containsNoSpecialCharacters'

  const CustomMetricDefinitionRecord = Record({
    metricName: '',
    definitionForVariants: {
      variantName: '',
      viewEventDefinition: {
        boxName: '',
        category: '',
        label: '',
        action: '',
        value: ''
      },
      successEventDefinition: {
        boxName: '',
        category: '',
        label: '',
        action: '',
        value: ''
      }
    }
  })

  export default {
    props: ['showHeader', 'readOnly', 'experiment', 'variants'],

    data () {
      return {
        items: this.initItemsFromExperiment(),
        error: '',
        customMetricDefinitionValid: true,
        editing: false,
        editedItem: {
          metricName: '',
          definitionForVariants: {
            variantName: '',
            viewEventDefinition: {},
            successEventDefinition: {}
          }
        },
        defaultItem: {
          boxName: '',
          category: '',
          label: '',
          action: '',
          value: ''
        },
        editedIndex: -1,
        headers: [
          {text: 'Type', value: 'type', align: 'left', sortable: false},
          {text: 'Variant', value: 'variantName', align: 'left', sortable: false},
          {text: 'BoxName', value: 'boxName', align: 'left', sortable: false},
          {text: 'Category', value: 'category', align: 'left', sortable: false},
          {text: 'Label', value: 'label', align: 'left', sortable: false},
          {text: 'Action', value: 'action', align: 'left', sortable: false},
          {text: 'Value', value: 'value', align: 'left', sortable: false}
        ],
        filterRules: [
          (v) => containsNoSpecialCharacters(v),
          (v) => !startsOrEndsWithSpace(v) || 'no spaces in the beginning or end'
        ],
        customMetricNameRules: [
          (v) => !!v || 'Name is required',
          (v) => containsNoSpecialCharacters(v),
          (v) => !startsOrEndsWithSpace(v) || 'no spaces in the beginning or end'
        ],
        variantRules: [
          (v) => !!v || this.isMetricAssignedToAllVariants || 'Variant is Required'
        ],
        defineCustomMetric: false,
        experimentVariants: [],
        isMetricAssignedToAllVariants: false,
        metricName: this.initNameFromExperiment()
      }
    },

    watch: {
      items: {
        handler: function (newValue) {
          if (this.validate() && newValue.length > 0) {
            this.$emit('input', this.buildResultFromValue())
          }
        },
        deep: true
      },

      variants: {
        handler: function (variants) {
          let v = Object.assign([], variants.variantNames)
          this.experimentVariants = v
          this.deleteCustomMetricsWithoutVariants(this.experimentVariants)
        },
        deep: true
      }
    },

    methods: {
      isMetricNameSet () {
        return !!this.metricName
      },
      notEveryVariantHaveCustomMetric () {
        return this.defineCustomMetric && this.items.length < this.experimentVariants.length
      },

      getVariants () {
        let variants = Object.assign([], this.experimentVariants)
        for (let item of this.items) {
          if (variants.includes(item.definitionForVariants.variantName) &&
            !(this.editedItem.definitionForVariants.variantName === item.definitionForVariants.variantName)) {
            const index = variants.indexOf(item.definitionForVariants.variantName)
            variants.splice(index, 1)
          }
        }
        return variants
      },

      deleteCustomMetricsWithoutVariants (variants) {
        for (let item of this.items) {
          if (!variants.includes(item.definitionForVariants.variantName)) {
            this.deleteItem(item)
          }
        }
      },

      haveOnlyBaseVariant () {
        return this.experimentVariants.length <= 1
      },

      haveEveryVariantMetric () {
        return this.experimentVariants.every(experiment => {
          return this.items.some(it => {
            return it.definitionForVariants.variantName === experiment
          })
        })
      },

      validateCustomMetrics () {
        if (this.defineCustomMetric) {
          return this.haveEveryVariantMetric()
        } else {
          return true
        }
      },

      initItemsFromExperiment () {
        if (!this.experiment || !this.experiment.customMetricDefinition) {
          return []
        }
        let metricName = this.experiment.customMetricDefinition.metricName
        let items = []
        this.experiment.customMetricDefinition.definitionForVariants.map(it => {
          items.push({
            metricName: metricName,
            definitionForVariants: it
          })
        })
        return items
      },

      initNameFromExperiment () {
        if (!this.experiment || !this.experiment.customMetricDefinition) {
          return ''
        }
        return this.experiment.customMetricDefinition.metricName
      },

      customMetricChange (val) {
        if (val === false) {
          this.items = []
          this.metricName = ''
        }
      },

      getItemsForTable () {
        let items = []
        for (let item of this.items) {
          let viewEventDefinition = Object.assign({}, item.definitionForVariants.viewEventDefinition)
          viewEventDefinition.definitionForVariants = Object.assign({}, item.definitionForVariants)
          viewEventDefinition.type = 'View event'
          viewEventDefinition.metricName = item.metricName
          viewEventDefinition.definitionForVariants.variantName = item.definitionForVariants.variantName
          let successEventDefinition = Object.assign({}, item.definitionForVariants.successEventDefinition)
          successEventDefinition.definitionForVariants = Object.assign({}, item.definitionForVariants)
          successEventDefinition.type = 'Success event'
          successEventDefinition.metricName = item.metricName
          successEventDefinition.definitionForVariants.variantName = item.definitionForVariants.variantName
          items.push(viewEventDefinition)
          items.push(successEventDefinition)
        }
        return items
      },

      changed () {
        if (this.editedIndex === -1) {
          return true
        }
        return JSON.stringify(this.editedItem) !== JSON.stringify(this.items[this.editedIndex])
      },

      close () {
        this.error = ''
        this.editing = false
        this.editedIndex = -1
        this.editedItem = {
          metricName: '',
          definitionForVariants: {
            variantName: '',
            viewEventDefinition: {},
            successEventDefinition: {}
          }
        }
      },

      newItem () {
        this.isMetricAssignedToAllVariants = false
        this.editing = true
        this.editedIndex = -1
        this.editedItem = Object.assign({}, {})
        this.editedItem.definitionForVariants = Object.assign({}, {})
        this.editedItem.metricName = this.metricName
        this.editedItem.definitionForVariants.variantName = ''
        this.editedItem.definitionForVariants.viewEventDefinition = Object.assign({}, this.defaultItem)
        this.editedItem.definitionForVariants.successEventDefinition = Object.assign({}, this.defaultItem)
      },

      editItem (item) {
        this.isMetricAssignedToAllVariants = false
        this.editing = true
        this.editedIndex = this.items.findIndex(it => it.definitionForVariants.variantName === item.definitionForVariants.variantName)
        this.editedItem.metricName = this.metricName

        this.editedItem.definitionForVariants.variantName = item.definitionForVariants.variantName
        this.editedItem.definitionForVariants.viewEventDefinition = Object.assign({}, item.definitionForVariants.viewEventDefinition)
        this.editedItem.definitionForVariants.successEventDefinition = Object.assign({}, item.definitionForVariants.successEventDefinition)
      },

      deleteItem (item) {
        const index = this.items.indexOf(item)
        this.items.splice(index, 1)
      },

      haveFormRequiredFields () {
        let successEventDefinitionCounter = Object.keys(this.editedItem.definitionForVariants.viewEventDefinition).filter(it => this.editedItem.definitionForVariants.viewEventDefinition[it] !== '').length
        let viewEventDefinitionCounter = Object.keys(this.editedItem.definitionForVariants.successEventDefinition).filter(it => this.editedItem.definitionForVariants.successEventDefinition[it] !== '').length
        return successEventDefinitionCounter >= 2 && viewEventDefinitionCounter >= 2 && this.editedItem.metricName !== ''
      },

      saveEditing () {
        if (this.$refs.customMetricDefinitionForm.validate() && this.haveFormRequiredFields()) {
          if (this.editedIndex > -1) {
            Object.assign(this.items[this.editedIndex], this.editedItem)
          } else {
            this.items.push(this.editedItem)
          }
          this.metricName = this.editedItem.metricName
          if (this.isMetricAssignedToAllVariants) {
            this.assignMetricToAllVariants(this.editedItem)
          }
          this.close()
        } else {
          this.error = 'You have to fulfil at least 2 fields in each event'
        }
      },

      validate () {
        return this.$refs.customMetricDefinitionForm.validate()
      },

      buildResult (value) {
        if (!value) {
          return List()
        }
        return List(value.map(item => new CustomMetricDefinitionRecord({
          metricName: item.metricName,
          definitionForVariants: {
            variantName: item.definitionForVariants.variantName,
            viewEventDefinition: {
              boxName: item.definitionForVariants.viewEventDefinition.boxName,
              category: item.definitionForVariants.viewEventDefinition.category,
              label: item.definitionForVariants.viewEventDefinition.label,
              action: item.definitionForVariants.viewEventDefinition.action,
              item: item.definitionForVariants.viewEventDefinition.item,
              value: item.definitionForVariants.viewEventDefinition.value
            },
            successEventDefinition: {
              boxName: item.definitionForVariants.successEventDefinition.boxName,
              category: item.definitionForVariants.successEventDefinition.category,
              label: item.definitionForVariants.successEventDefinition.label,
              action: item.definitionForVariants.successEventDefinition.action,
              item: item.definitionForVariants.successEventDefinition.item,
              value: item.definitionForVariants.successEventDefinition.value
            }
          }
        })))
      },

      buildResultFromValue () {
        return this.buildResult(this.items)
      },

      assignMetricToAllVariants (metric) {
        this.deleteItem(metric)
        for (let variant of this.experimentVariants) {
          let e = Object.assign({}, metric)
          e.definitionForVariants = Object.assign({}, metric.definitionForVariants)
          e.definitionForVariants.variantName = variant
          this.items.push(e)
        }
      }
    }
  }
</script>
