import Vue from 'vue'
import App from './App.vue'
import router from './router'
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import '@/assets/global.css'
import SlideVerify from 'vue-monoplasty-slide-verify';
import * as echarts from 'echarts';

Vue.use(SlideVerify);
Vue.config.productionTip = false
Vue.use(ElementUI, { size: 'small' });  // medium  small mini

// 将echarts库挂载到Vue原型上，方便全局调用
Vue.prototype.$echarts = echarts;

new Vue({
  router,
  render: h => h(App)
}).$mount('#app')
