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
              <v-select label="variant" v-model="editedItem.definitionForVariant.variantName"
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
                      <v-text-field label="boxName" v-model="editedItem.definitionForVariant.viewEventDefinition.boxName"
                                    :rules="filterRules"
                      ></v-text-field>
                    </v-flex>
                  </v-layout>

                  <v-layout row align-center>
                    <v-flex offset-xs2>
                      <v-text-field label="category" v-model="editedItem.definitionForVariant.viewEventDefinition.category"
                                    :rules="filterRules"
                      ></v-text-field>
                    </v-flex>
                  </v-layout>

                  <v-layout row align-center>
                    <v-flex offset-xs2>
                      <v-text-field label="label" v-model="editedItem.definitionForVariant.viewEventDefinition.label"
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
                      <v-text-field label="action" v-model="editedItem.definitionForVariant.viewEventDefinition.action"
                                    :rules="filterRules"
                      ></v-text-field>
                    </v-flex>
                  </v-layout>

                  <v-layout row align-center>
                    <v-flex offset-xs2>
                      <v-text-field label="value" v-model="editedItem.definitionForVariant.viewEventDefinition.value"
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
                      <v-text-field label="boxName" v-model="editedItem.definitionForVariant.successEventDefinition.boxName"
                                    :rules="filterRules"
                      ></v-text-field>
                    </v-flex>
                  </v-layout>

                  <v-layout row align-center>
                    <v-flex offset-xs2>
                      <v-text-field label="category" v-model="editedItem.definitionForVariant.successEventDefinition.category"
                                    :rules="filterRules"
                      ></v-text-field>
                    </v-flex>
                  </v-layout>

                  <v-layout row align-center>
                    <v-flex offset-xs2>
                      <v-text-field label="label" v-model="editedItem.definitionForVariant.successEventDefinition.label"
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
                      <v-text-field label="action" v-model="editedItem.definitionForVariant.successEventDefinition.action"
                                    :rules="filterRules"
                      ></v-text-field>
                    </v-flex>
                  </v-layout>

                  <v-layout row align-center>
                    <v-flex offset-xs2>
                      <v-text-field label="value" v-model="editedItem.definitionForVariant.successEventDefinition.value"
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
          <td>{{ props.item.metricName }}</td>
          <td>{{ props.item.definitionForVariant.variantName }}</td>
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
  </div>
</template>

<script>
  import {startsOrEndsWithSpace} from '../../utils/startsOrEndsWithSpace'
  import { Record, List } from 'immutable'
  import {containsNoSpecialCharacters} from '../../utils/containsNoSpecialCharacters'

  const CustomMetricDefinitionRecord = Record({
    metricName: '',
    definitionForVariant: {
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
        items: this.initFromExperiment(),
        error: '',
        customMetricDefinitionValid: true,
        editing: false,
        editedItem: {
          metricName: '',
          definitionForVariant: {
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
          {text: 'Name', value: 'metricName', align: 'left', sortable: false},
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
        metricName: ''
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
          if (variants.includes(item.definitionForVariant.variantName) &&
            !(this.editedItem.definitionForVariant.variantName === item.definitionForVariant.variantName)) {
            const index = variants.indexOf(item.definitionForVariant.variantName)
            variants.splice(index, 1)
          }
        }
        return variants
      },

      deleteCustomMetricsWithoutVariants (variants) {
        for (let item of this.items) {
          if (!variants.includes(item.definitionForVariant.variantName)) {
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
            return it.definitionForVariant.variantName === experiment
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

      initFromExperiment () {
        if (!this.experiment || !this.experiment.customMetricsDefinition) {
          return []
        }
        let metricName = this.experiment.customMetricsDefinition.definitionForVariant[0].metricName
        let items = []
        this.experiment.customMetricsDefinition.definitionForVariant.map(it => {
          items.push({
            metricName: metricName,
            definitionForVariant: it
          })
        })
        return items
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
          let viewEventDefinition = Object.assign({}, item.definitionForVariant.viewEventDefinition)
          viewEventDefinition.definitionForVariant = Object.assign({}, item.definitionForVariant)
          viewEventDefinition.type = 'View event'
          viewEventDefinition.metricName = item.metricName
          viewEventDefinition.definitionForVariant.variantName = item.definitionForVariant.variantName
          let successEventDefinition = Object.assign({}, item.definitionForVariant.successEventDefinition)
          successEventDefinition.definitionForVariant = Object.assign({}, item.definitionForVariant)
          successEventDefinition.type = 'Success event'
          successEventDefinition.metricName = item.metricName
          successEventDefinition.definitionForVariant.variantName = item.definitionForVariant.variantName
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
          definitionForVariant: {
            variantName: '',
            viewEventDefinition: {},
            successEventDefinition: {}
          }
        }
      },

      newItem () {
        this.editing = true
        this.editedIndex = -1
        this.editedItem = Object.assign({}, {})
        this.editedItem.definitionForVariant = Object.assign({}, {})
        this.editedItem.metricName = this.metricName
        this.editedItem.definitionForVariant.variantName = ''
        this.editedItem.definitionForVariant.viewEventDefinition = Object.assign({}, this.defaultItem)
        this.editedItem.definitionForVariant.successEventDefinition = Object.assign({}, this.defaultItem)
      },

      editItem (item) {
        this.editing = true
        this.editedIndex = 0
        this.editedItem.metricName = this.metricName

        this.editedItem.definitionForVariant.variantName = item.definitionForVariant.variantName
        this.editedItem.definitionForVariant.viewEventDefinition = Object.assign({}, item.definitionForVariant.viewEventDefinition)
        this.editedItem.definitionForVariant.successEventDefinition = Object.assign({}, item.definitionForVariant.successEventDefinition)
      },

      deleteItem (item) {
        const index = this.items.indexOf(item)
        this.items.splice(index, 1)
      },

      haveFormRequiredFields () {
        let successEventDefinitionCounter = Object.keys(this.editedItem.definitionForVariant.viewEventDefinition).filter(it => this.editedItem.definitionForVariant.viewEventDefinition[it] !== '').length
        let viewEventDefinitionCounter = Object.keys(this.editedItem.definitionForVariant.successEventDefinition).filter(it => this.editedItem.definitionForVariant.successEventDefinition[it] !== '').length
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
          this.isMetricAssignedToAllVariants = false
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
          definitionForVariant: {
            variantName: item.definitionForVariant.variantName,
            viewEventDefinition: {
              boxName: item.definitionForVariant.viewEventDefinition.boxName,
              category: item.definitionForVariant.viewEventDefinition.category,
              label: item.definitionForVariant.viewEventDefinition.label,
              action: item.definitionForVariant.viewEventDefinition.action,
              item: item.definitionForVariant.viewEventDefinition.item,
              value: item.definitionForVariant.viewEventDefinition.value
            },
            successEventDefinition: {
              boxName: item.definitionForVariant.successEventDefinition.boxName,
              category: item.definitionForVariant.successEventDefinition.category,
              label: item.definitionForVariant.successEventDefinition.label,
              action: item.definitionForVariant.successEventDefinition.action,
              item: item.definitionForVariant.successEventDefinition.item,
              value: item.definitionForVariant.successEventDefinition.value
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
          e.definitionForVariant = Object.assign({}, metric.definitionForVariant)
          e.definitionForVariant.variantName = variant
          this.items.push(e)
        }
      }
    }
  }
</script>
