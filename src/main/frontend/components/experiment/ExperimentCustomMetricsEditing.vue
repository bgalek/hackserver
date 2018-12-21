<template>
  <div>
    <v-layout row v-if="showHeader">
      <v-flex offset-xs1>
        <h3 class="mt-3 blue--text">Custom metrics definition</h3>
      </v-flex>
    </v-layout>
    <v-flex offset-xs1>
      <v-switch
        label="Define custom metric"
        v-model="defineCustomMetric"
        v-on:change="customMetricChange"
      ></v-switch>
    </v-flex>
    <v-flex offset-xs1>
      <v-btn color="primary" class="mb-2" @click="newItem()" v-if="defineCustomMetric && items.length < 1">
        Add custom metric
      </v-btn>
      <div v-else-if="defineCustomMetric && items.length > 0">
        <v-btn icon class="mx-0" @click="editItem()">
          <v-icon color="teal">edit</v-icon>
        </v-btn>
        {{getItemName()}}
      </div>
    </v-flex>


    <v-dialog v-model="editing" max-width="450px">
      <v-form v-model="customMetricDefinitionValid" ref="customMetricDefinitionForm">
        <v-card>
          <v-card-title>
            <span class="headline">Custom metric name</span>
          </v-card-title>
          <v-layout fluid class="pa-3 ma-0">
            <v-flex>
              <v-text-field label="name" v-model="editedItem.name"
                            :rules="customMetricRules"

              ></v-text-field>
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
                      <v-text-field label="boxName" v-model="editedItem.viewEventDefinition.boxName"
                                    :rules="filterRules"
                      ></v-text-field>
                    </v-flex>
                  </v-layout>

                  <v-layout row align-center>
                    <v-flex offset-xs2>
                      <v-text-field label="category" v-model="editedItem.viewEventDefinition.category"
                                    :rules="filterRules"
                      ></v-text-field>
                    </v-flex>
                  </v-layout>

                  <v-layout row align-center>
                    <v-flex offset-xs2>
                      <v-text-field label="label" v-model="editedItem.viewEventDefinition.label"
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
                      <v-text-field label="action" v-model="editedItem.viewEventDefinition.action"
                                    :rules="filterRules"
                      ></v-text-field>
                    </v-flex>
                  </v-layout>

                  <v-layout row align-center>
                    <v-flex offset-xs2>
                      <v-text-field label="value" v-model="editedItem.viewEventDefinition.value"
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
                      <v-text-field label="boxName" v-model="editedItem.successEventDefinition.boxName"
                                    :rules="filterRules"
                      ></v-text-field>
                    </v-flex>
                  </v-layout>

                  <v-layout row align-center>
                    <v-flex offset-xs2>
                      <v-text-field label="category" v-model="editedItem.successEventDefinition.category"
                                    :rules="filterRules"
                      ></v-text-field>
                    </v-flex>
                  </v-layout>

                  <v-layout row align-center>
                    <v-flex offset-xs2>
                      <v-text-field label="label" v-model="editedItem.successEventDefinition.label"
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
                      <v-text-field label="action" v-model="editedItem.successEventDefinition.action"
                                    :rules="filterRules"
                      ></v-text-field>
                    </v-flex>
                  </v-layout>

                  <v-layout row align-center>
                    <v-flex offset-xs2>
                      <v-text-field label="value" v-model="editedItem.successEventDefinition.value"
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
    <v-flex offset-xs1>
      <v-data-table
        v-if="defineCustomMetric"
        :headers="headers"
        :items="getItemsForTable()"
        hide-actions
        light
        offset-xs1
      >

        <template slot="items" slot-scope="props">
          <td>{{ props.item.type }}</td>
          <td>{{ props.item.boxName }}</td>
          <td>{{ props.item.category }}</td>
          <td>{{ props.item.label }}</td>
          <td>{{ props.item.action }}</td>
          <td>{{ props.item.value }}</td>

        </template>
      </v-data-table>
    </v-flex>
  </div>
</template>

<script>
  import {startsOrEndsWithSpace} from '../../utils/startsOrEndsWithSpace'
  import {Record} from 'immutable'
  import {containsNoSpecialCharacters} from '../../utils/containsNoSpecialCharacters'

  const CustomMetricDefinitionRecord = Record({
    name: '',
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
  })

  export default {
    props: ['showHeader', 'readOnly'],

    data () {
      return {
        items: [],
        error: '',
        customMetricDefinitionValid: true,
        editing: false,
        editedItem: {
          name: '',
          viewEventDefinition: {},
          successEventDefinition: {}
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
        customMetricRules: [
          (v) => !!v || 'Name is required',
          (v) => containsNoSpecialCharacters(v),
          (v) => !startsOrEndsWithSpace(v) || 'no spaces in the beginning or end'
        ],
        defineCustomMetric: false
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
      }
    },

    methods: {
      customMetricChange (val) {
        if (val === false) {
          this.items = []
          this.onDefineCustomMetricChange(false)
        }
      },
      getItemName () {
        return this.items[0].name
      },

      getItemsForTable () {
        let items = []
        for (let item of this.items) {
          let viewEventDefinition = Object.assign({}, item.viewEventDefinition)
          viewEventDefinition.type = 'View event'
          let successEventDefinition = Object.assign({}, item.successEventDefinition)
          successEventDefinition.type = 'Success event'
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
          name: '',
          viewEventDefinition: {},
          successEventDefinition: {}
        }
      },

      newItem () {
        this.editing = true
        this.editedIndex = -1
        this.editedItem = Object.assign({}, {})
        this.editedItem.name = ''
        this.editedItem.viewEventDefinition = Object.assign({}, this.defaultItem)
        this.editedItem.successEventDefinition = Object.assign({}, this.defaultItem)
      },

      editItem () {
        let item = this.items[0]
        this.editing = true
        this.editedIndex = 0
        this.editedItem.name = item.name
        this.editedItem.viewEventDefinition = Object.assign({}, item.viewEventDefinition)
        this.editedItem.successEventDefinition = Object.assign({}, item.successEventDefinition)
      },

      deleteItem (item) {
        const index = this.items.indexOf(item)
        this.items.splice(index, 1)
      },

      haveFormRequiredFields () {
        let successEventDefinitionCounter = Object.keys(this.editedItem.viewEventDefinition).filter(it => this.editedItem.viewEventDefinition[it] !== '').length
        let viewEventDefinitionCounter = Object.keys(this.editedItem.successEventDefinition).filter(it => this.editedItem.successEventDefinition[it] !== '').length
        return successEventDefinitionCounter >= 2 && viewEventDefinitionCounter >= 2 && this.editedItem.name !== ''
      },

      saveEditing () {
        if (this.$refs.customMetricDefinitionForm.validate() && this.haveFormRequiredFields()) {
          if (this.editedIndex > -1) {
            Object.assign(this.items[this.editedIndex], this.editedItem)
          } else {
            this.items.push(this.editedItem)
          }
          this.onDefineCustomMetricChange(this.editedItem)
          this.close()
        } else {
          this.error = 'You have to fulfil at least 2 fields in each event'
        }
      },

      validate () {
        return this.$refs.customMetricDefinitionForm.validate()
      },

      buildResult (value) {
        return new CustomMetricDefinitionRecord({
          name: value[0].name,
          successEventDefinition: {
            boxName: value[0].successEventDefinition.boxName,
            category: value[0].successEventDefinition.category,
            label: value[0].successEventDefinition.label,
            action: value[0].successEventDefinition.action,
            value: value[0].successEventDefinition.value
          },
          viewEventDefinition: {
            boxName: value[0].viewEventDefinition.boxName,
            category: value[0].viewEventDefinition.category,
            label: value[0].viewEventDefinition.label,
            action: value[0].viewEventDefinition.action,
            value: value[0].viewEventDefinition.value
          }

        })
      },

      buildResultFromValue () {
        return this.buildResult(this.items)
      },

      onDefineCustomMetricChange (newVal) {
        this.$emit('customMetric', newVal)
      }
    }
  }
</script>
