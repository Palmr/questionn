<template>
    <div>
        <h2>Answers</h2>
        <li v-for="answer in answers" :key="answer.name">
            {{ answer.name }} - {{ answer.title }}
        </li>
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
