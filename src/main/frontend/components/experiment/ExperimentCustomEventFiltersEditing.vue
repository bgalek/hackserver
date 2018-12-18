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
      ></v-switch>
    </v-flex>
    <v-flex offset-xs1>
      <v-btn color="primary" class="mb-2" @click="newItem()" v-if="defineCustomMetric">
        Add custom metric
      </v-btn>
    </v-flex>



    <v-dialog v-model="editing" max-width="450px">
      <v-form v-model="customEventDefinitionValid" ref="customEventDefinitionForm">
        <v-card>
          <v-card-title>
            <span class="headline">View event definition</span>
          </v-card-title>
          <v-card-text>

            <v-container fluid class="pa-3 ma-0">

              <v-layout row align-center>
                <v-flex xs2>
                  <v-tooltip top close-delay="3000">
                    For example: <code>offers_reco</code>
                    <v-icon slot="activator">help_outline</v-icon>
                  </v-tooltip>
                </v-flex>
                <v-layout row align-center>
                  <v-flex>
                    <v-text-field label="name" v-model="editedItem.name"
                                  :rules="filterRules"
                    ></v-text-field>
                  </v-flex>
                </v-layout>

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

            </v-container>

          </v-card-text>
          <v-card-title>
            <span class="headline">Action event definition</span>
          </v-card-title>
          <v-card-text>

            <v-container fluid class="pa-3 ma-0">

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
          <v-card-actions>
            <v-spacer></v-spacer>

            <v-btn flat @click="close()">Cancel</v-btn>
            <v-btn color="gray"
                   :disabled="!changed()"
                   @click="saveEditing()"
                   style="text-transform: none">Save
            </v-btn>

          </v-card-actions>
        </v-card>
      </v-form>
    </v-dialog>
    <v-flex offset-xs1>
    <v-data-table
      v-if="defineCustomMetric"
      :headers="headers"
      :items="items"
      hide-actions
      light
      offset-xs1
    >

      <template slot="items" slot-scope="props">
        <td>{{ props.item}}</td>
        <!--<td>{{ props.item.category }}</td>-->
        <!--<td>{{ props.item.label }}</td>-->
        <!--<td>{{ props.item.action }}</td>-->
        <!--<td>{{ props.item.value }}</td>-->

        <td class="justify-center layout px-0" v-if="!readOnly">
          <v-btn icon class="mx-0" @click="editItem(props.item)">
            <v-icon color="teal">edit</v-icon>
          </v-btn>
          <v-btn icon class="mx-0" @click="deleteItem(props.item)">
            <v-icon color="pink">delete</v-icon>
          </v-btn>
        </td>

      </template>
    </v-data-table>
    </v-flex>
  </div>
</template>

<script>
  import {startsOrEndsWithSpace} from '../../utils/startsOrEndsWithSpace'
  import {Record} from 'immutable'

  const CustomEventDefinitionRecord = Record({
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
    props: ['readOnly', 'experiment', 'showHeader'],

    data() {
      return {
        items: this.initFromExperiment(),
        customEventDefinitionValid: true,
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
          {text: 'BoxName', value: 'boxName', align: 'left', sortable: false},
          {text: 'Category', value: 'category', align: 'left', sortable: false},
          {text: 'Label', value: 'label', align: 'left', sortable: false},
          {text: 'Action', value: 'action', align: 'left', sortable: false},
          {text: 'Value', value: 'value', align: 'left', sortable: false}
        ],
        filterRules: [
          (v) => this.containsNo(v, '*') || 'no *',
          (v) => this.containsNo(v, '?') || 'no ?',
          (v) => this.containsNo(v, '%') || 'no %',
          (v) => this.containsNo(v, '\'') || 'no \'',
          (v) => this.containsNo(v, '/') || 'no /',
          (v) => this.containsNo(v, '\\') || 'no \\',
          (v) => this.containsNo(v, '"') || 'no "',
          (v) => !startsOrEndsWithSpace(v) || 'no spaces in the beginning or end'
        ],
        defineCustomMetric: false
      }
    },

    computed: {
      givenValue: function () {
        return this.buildResult(this.initFromExperiment())
      }
    },

    watch: {
      items: {
        handler: function (newValue) {
          if (this.validate()) {
            this.$emit('input', this.buildResultFromValue())
          }
        },
        deep: true
      },
      experiment: {
        handler: function (newValue) {
          this.items = this.initFromExperiment()
        },
        deep: true
      }
    },

    methods: {
      initFromExperiment() {
        if (!this.experiment || !this.experiment.customEventDefinitions) {
          return []
        }
        return this.experiment.customEventDefinitions.toArray().map(it => Object.assign({}, it))
      },

      containsNo(str, char) {
        if (!str) {
          return true
        }

        return str.indexOf(char) === -1
      },

      changed() {
        if (this.editedIndex === -1) {
          return true
        }
        return JSON.stringify(this.editedItem) !== JSON.stringify(this.items[this.editedIndex])
      },

      close() {
        this.editing = false
        this.editedIndex = -1
        this.editedItem = {
          name: '',
          viewEventDefinition: {},
          successEventDefinition: {}
        }
      },

      newItem() {
        this.editing = true
        this.editedIndex = -1
        this.editedItem = Object.assign({}, {})
        this.editedItem.name = Object.assign('', this.name)
        this.editedItem.viewEventDefinition = Object.assign({}, this.defaultItem)
        this.editedItem.successEventDefinition = Object.assign({}, this.defaultItem)
      },

      editItem(item) {
        this.editing = true
        this.editedIndex = this.items.indexOf(item)
        this.editedItem.viewEventDefinition = Object.assign({}, item.viewEventDefinition)
        this.editedItem.successEventDefinition = Object.assign({}, item.successEventDefinition)
      },

      deleteItem(item) {
        const index = this.items.indexOf(item)
        this.items.splice(index, 1)
      },

      saveEditing() {
        if (this.$refs.customEventDefinitionForm.validate()) {
          if (this.editedIndex > -1) {
            Object.assign(this.items[this.editedIndex], this.editedItem)
          } else {
            this.items.push(this.editedItem)
          }
          console.log('lol')
          console.log(this.editedItem)
          this.onDefineCustomMetricChange(this.editedItem)
          this.close()
        }
      },

      validate() {
        return this.$refs.customEventDefinitionForm.validate()
      },

      buildResult(value) {
        console.log(value)
          return new CustomEventDefinitionRecord({
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

      buildResultFromValue() {
        return this.buildResult(this.items)
      },

      onDefineCustomMetricChange (newVal) {
        console.log('chuj')
        console.log(newVal)
        this.$emit('customMetric', newVal)
      }
    }
  }
</script>
