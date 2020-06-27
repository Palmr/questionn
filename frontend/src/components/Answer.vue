<template>
    <div>
        <v-card>
            <v-card-title>{{ answerDetails.title }}</v-card-title>
            <v-card-subtitle>{{ answerDetails.description }}</v-card-subtitle>

            <v-container fluid>
                <v-row dense>
                    <v-col v-for="param in queryParameters" :key="param.name" cols="1">
                        <v-text-field :label="param.name" v-model="param.value"></v-text-field>
                    </v-col>
                </v-row>
            </v-container>

            <v-card-actions>
                <v-btn @click="doScience" color="primary">Do some science :3</v-btn>
            </v-card-actions>
        </v-card>
        <v-card v-if="result">
            <v-card-title>Result</v-card-title>

            <v-data-table
            :headers="result.headers"
            :items="result.items"
            :items-per-page="5"
            ></v-data-table>
        </v-card>
    </div>
</template>

<script>
    import questionnApi from "../questionnApi";

    export default {
        name: 'Answer',
        data: () => ({
            answerDetails: {},
            queryParameters: [],
            result: null,
        }),
        created() {
            let answerName = this.$route.params.answerName;
            questionnApi.getAnswer(answerName).then(result => this.answerDetails = result);
            questionnApi.getAnswerParameters(answerName).then(result => this.queryParameters = result);
        },
        methods: {
            doScience: function () {
                let params = this.queryParameters
                .filter(param => param.value)
                .reduce((a, param) => {
                    a[param.name] = param.value
                    return a;
                }, {});

                questionnApi.executeAnswer(this.answerDetails.name, params)
                .then(r => {
                    let headers = r.metadataRow.fieldNames.map(name => {
                        return {
                            text: name,
                            value: name,
                        };
                    });

                    let items = r.dataRows.map(row => {
                        let item = {};
                        for (let i = 0; i < row.fields.length; i++) {
                            item[r.metadataRow.fieldNames[i]] = row.fields[i];
                        }
                        return item;
                    });

                    this.result = {
                        headers: headers,
                        items: items,
                    }
                })
                .catch(e => console.error(e));
            },
        }
    };
</script>
