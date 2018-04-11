<template>
    <div>
      <v-btn color="primary" class="mb-2" @click="newItem()">
        Add event
      </v-btn>
      <v-dialog v-model="editing" max-width="500px">
        <v-card>
          <v-card-title>
            <span class="headline">Event definition</span>
          </v-card-title>
          <v-card-text>
            <v-container grid-list-md>
              <v-layout wrap>
                <v-flex xs12 sm6 md4>
                  <v-select
                    :items="variants"
                    v-model="editedItem.variant"
                    label="variant"
                  ></v-select>
                </v-flex>
                <v-flex xs12 sm6 md4>
                  <v-text-field label="category" v-model="editedItem.category"></v-text-field>
                </v-flex>
                <v-flex xs12 sm6 md4>
                  <v-text-field label="label" v-model="editedItem.label"></v-text-field>
                </v-flex>
                <v-flex xs12 sm6 md4>
                  <v-text-field label="action" v-model="editedItem.action"></v-text-field>
                </v-flex>
                <v-flex xs12 sm6 md4>
                  <v-text-field label="value" v-model="editedItem.value"></v-text-field>
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
      </v-dialog>

      <v-data-table
        :headers="headers"
        :items="items"
        hide-actions
        class="elevation-1"
      >
        <template slot="items" slot-scope="props">
          <td>{{ props.item.variant }}</td>
          <td>{{ props.item.category }}</td>
          <td>{{ props.item.label }}</td>
          <td>{{ props.item.action }}</td>
          <td>{{ props.item.value }}</td>

          <td class="justify-center layout px-0">
            <v-btn icon class="mx-0" @click="editItem(props.item)">
              <v-icon color="teal">edit</v-icon>
            </v-btn>
            <v-btn icon class="mx-0" @click="deleteItem(props.item)">
              <v-icon color="pink">delete</v-icon>
            </v-btn>
          </td>

        </template>
      </v-data-table>
    </div>
</template>

<script>
  export default {
    props: ['variants'],

    data () {
      return {
        editing: false,
        editedItem: {},
        defaultItem: {
          variant: 'base',
          category: '',
          label: '',
          action: '',
          value: ''
        },
        editedIndex: -1,
        headers: [
          { text: 'Variant', value: 'variant', align: 'left', class: 'subheading', sortable: false },
          { text: 'Category', value: 'category', align: 'left', class: 'subheading', sortable: false },
          { text: 'Label', value: 'label', align: 'left', class: 'subheading', sortable: false },
          { text: 'Action', value: 'action', align: 'left', class: 'subheading', sortable: false },
          { text: 'Value', value: 'value', align: 'left', class: 'subheading', sortable: false }
        ],
        items: [],
        filterRules: [
          (v) => this.isUrlValid(v) || 'not a valid URL'
        ]
      }
    },

    methods: {
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
        this.$emit('input', this.items)
      },

      saveEditing () {
        if (this.editedIndex > -1) {
          Object.assign(this.items[this.editedIndex], this.editedItem)
        } else {
          this.items.push(this.editedItem)
        }
        this.close()
        this.$emit('input', this.items)
      }
    }
  }
</script>
