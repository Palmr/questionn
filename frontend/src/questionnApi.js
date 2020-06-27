const apiUrl = 'http://localhost:5050/api/';

function getAnswers() {
    return fetch(apiUrl + 'answers')
        .then(r => r.json());
}

function getAnswer(answerName) {
    return fetch(apiUrl + 'answers/' + answerName)
        .then(r => r.json());
}

function getAnswerParameters(answerName) {
    return fetch(apiUrl + 'answers/' + answerName + '/params')
        .then(r => r.json());
}

function executeAnswer(answerName, params) {
    let init = {
        method: 'POST',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(params)
    };
    return fetch(apiUrl + 'answers/' + answerName, init)
        .then(r => {
            if (!r.ok) {
                throw new Error('Failed to execute answer');
            }
            return r.json();
        });
}

export default {
    getAnswers,
    getAnswer,
    getAnswerParameters,
    executeAnswer,
}