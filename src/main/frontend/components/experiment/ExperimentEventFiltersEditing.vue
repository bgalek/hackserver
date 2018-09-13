<template>
    <div>
      <v-btn color="primary" class="mb-2" @click="newItem()" v-if="!readOnly">
        Add event
      </v-btn>
      <v-dialog v-model="editing" max-width="500px">
        <v-form v-model="eventDefinitionValid" ref="eventDefinitionForm">
        <v-card>
          <v-card-title>
            <span class="headline">Event definition</span>
          </v-card-title>
          <v-card-text>
            <v-container grid-list-md>
              <v-layout wrap>
                <v-flex xs12 sm6 md4>
                  <v-text-field label="boxName" v-model="editedItem.boxName" hint="for example: offers_reco"
                                :rules="filterRules"
                  ></v-text-field>
                </v-flex>
                <v-flex xs12 sm6 md4>
                  <v-text-field label="category" v-model="editedItem.category"
                                :rules="filterRules"
                  ></v-text-field>
                </v-flex>
                <v-flex xs12 sm6 md4>
                  <v-text-field label="label" v-model="editedItem.label"
                                :rules="filterRules"
                  ></v-text-field>
                </v-flex>
                <v-flex xs12 sm6 md4>
                  <v-text-field label="action" v-model="editedItem.action" hint="for example: boxView"
                                :rules="filterRules"
                  ></v-text-field>
                </v-flex>
                <v-flex xs12 sm6 md4>
                  <v-text-field label="value" v-model="editedItem.value"
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

      <v-data-table
        :headers="headers"
        :items="items"
        hide-actions
         light
      >
        <template slot="items" slot-scope="props">
          <td>{{ props.item.boxName }}</td>
          <td>{{ props.item.category }}</td>
          <td>{{ props.item.label }}</td>
          <td>{{ props.item.action }}</td>
          <td>{{ props.item.value }}</td>

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

      <v-layout row align-center v-if="showButtons">
        <v-flex>
          <v-btn flat @click="closeEventDefinitions">Cancel</v-btn>
        </v-flex>

        <v-flex>
          <v-btn color="gray"
                 :disabled="!eventDefinitionsChanged() || !this.eventDefinitionValid"
                 @click="updateEventDefinitions"
                 style="text-transform: none">
            Update NGA event definitions of &nbsp;<b>{{ this.experiment.id }}</b>
          </v-btn>
        </v-flex>
      </v-layout>
    </div>
</template>

<script>
  import {startsOrEndsWithSpace} from '../../utils/startsOrEndsWithSpace'
  import { Record, List } from 'immutable'

  const EventDefinitionRecord = Record({
    boxName: '',
    category: '',
    label: '',
    action: '',
    value: ''
  })

  export default {
    props: ['readOnly', 'experiment', 'showButtons'],

    data () {
      const initialValue = this.init(this.experiment)

      return {
        givenValue: this.buildResult(initialValue),
        items: initialValue,
        eventDefinitionValid: true,
        editing: false,
        editedItem: {},
        defaultItem: {
          boxName: '',
          category: '',
          label: '',
          action: '',
          value: ''
        },
        editedIndex: -1,
        headers: [
          { text: 'BoxName', value: 'boxName', align: 'left', sortable: false },
          { text: 'Category', value: 'category', align: 'left', sortable: false },
          { text: 'Label', value: 'label', align: 'left', sortable: false },
          { text: 'Action', value: 'action', align: 'left', sortable: false },
          { text: 'Value', value: 'value', align: 'left', sortable: false }
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
        ]
      }
    },

    watch: {
      items: {
        handler: function (newValue) {
          console.log("watch")
          if (this.validate()) {
            this.$emit('input', this.buildResultFromValue())
          }
        },
        deep: true
      }
    },

    methods: {
      init (experiment) {
        if (!experiment || !experiment.eventDefinitions) {
          return []
        }

        return experiment.eventDefinitions.toArray()
      },

      containsNo (str, char) {
        if (!str) {
          return true
        }

        return str.indexOf(char) === -1
      },

      changed () {
        if (this.editedIndex === -1) {
          return true
        }
        return JSON.stringify(this.editedItem) !== JSON.stringify(this.items[this.editedIndex])
      },

      close () {
        this.editing = false
        this.editedIndex = -1
        this.editedItem = {}
      },

      newItem () {
        this.editing = true
        this.editedIndex = -1
        this.editedItem = Object.assign({}, this.defaultItem)
      },

      editItem (item) {
        this.editing = true
        this.editedIndex = this.items.indexOf(item)
        this.editedItem = Object.assign({}, item)
      },

      deleteItem (item) {
        const index = this.items.indexOf(item)
        this.items.splice(index, 1)
      },

      saveEditing () {
        if (this.$refs.eventDefinitionForm.validate()) {
          if (this.editedIndex > -1) {
            Object.assign(this.items[this.editedIndex], this.editedItem)
          } else {
            this.items.push(this.editedItem)
          }
          this.close()
        }
      },

      validate () {
        return this.$refs.eventDefinitionForm.validate()
      },

      buildResult (value) {
        if (!value) {
          return List()
        }

        return List(value.map(item => new EventDefinitionRecord({
          boxName: item.boxName,
          category: item.category,
          label: item.label,
          action: item.action,
          value: item.value
        })))
      },

      buildResultFromValue () {
        return this.buildResult(this.items)
      },

      updateEventDefinitions () {
        const newValue = this.buildResultFromValue()
        this.givenValue = newValue
        this.$emit('updateEventDefinitions', newValue)
      },

      closeEventDefinitions () {
        this.value = this.init(this.experiment)
        this.$emit('closeEventDefinitions')
      },

      eventDefinitionsChanged () {
        return !this.givenValue.equals(this.buildResultFromValue())
      }
    }
  }
</script>
