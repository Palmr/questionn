const apiUrl = 'http://localhost:5050/api/';

function getAnswers() {
    return fetch(apiUrl + 'answers')
        .then(r => r.json());
}

export default {
    getAnswers,
}