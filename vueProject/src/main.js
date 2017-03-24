// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue';
import VueRouter from 'vue-router';
import VueResource from 'vue-resource';
import App from './App';
import goods from './components/goods.vue';
import comments from './components/comments.vue';
import sellers from './components/sellers.vue';

// Vue.config.productionTip = false;
// eslint-disable-next-line
/* eslint-disable */
/* eslint-disable no-new */

// 模块化编程
Vue.use(VueRouter);
Vue.use(VueResource);
var routes = [
    { path: '/goods', component: goods },
    { path: '/sellers', component: sellers },
    { path: '/comments', component: comments }
];
var router=new VueRouter({
    routes,
    linkActiveClass:'active'//配置激活状态是的class
});
var app=Vue.extend(App);
new app({
    router
}).$mount('#app');

router.push('/goods');
