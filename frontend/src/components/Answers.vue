<template>
    <div>
        <v-container fluid>
            <v-row dense>
                <v-col v-for="answer in answers" :key="answer.name" cols="3">
                    <v-card>
                        <v-card-title v-text="answer.title"></v-card-title>
                        <v-card-subtitle>{{ answer.description }}</v-card-subtitle>

                        <v-card-actions>
                            <v-btn text>Run</v-btn>
                        </v-card-actions>
                    </v-card>
                </v-col>
            </v-row>
        </v-container>
    </div>
</template>

<script>
    import questionnApi from "../questionnApi";

    export default {
        name: 'Answers',

        data: () => ({
            answers: [],
        }),
        created() {
            questionnApi.getAnswers()
            .then(answers => this.answers = answers);
        },
    };
</script>
