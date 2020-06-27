import Vue from 'vue';
import VueRouter from 'vue-router';
import Answer from '../components/Answer';
import Answers from '../components/Answers';
import Dashboards from '../components/Dashboards';
import Settings from '../components/Settings';
import Debug from '../components/Debug';

Vue.use(VueRouter);

const routes = [
    { path: '/answers', component: Answers },
    { path: '/answer/:answerName', component: Answer },
    { path: '/dashboards', component: Dashboards },
    { path: '/settings', component: Settings },
    { path: '/debug', component: Debug },
    { path : '*', redirect : '/answers' },
];

export default new VueRouter({ routes });
