<template>
  <div id="app">
      <v-header></v-header>
  <div class="tab">
    <router-link to="/goods" class="tab-item" >商品</router-link>
      <router-link to="/comments" class="tab-item">评论</router-link>
      <router-link to="/sellers" class="tab-item">商家</router-link>
  </div>
    <router-view></router-view>
  </div>
</template>

<script>
//    注册header组件
// eslint-disable-next-line
/* eslint-disable */
    import header from './components/header.vue';
    export default {
        components:{
          'v-header':header
        }
    };
</script>

<style lang="stylus" rel="stylesheet/stylus">
bgcolor(el)
    background el
.tab
  display flex
  /*justify-content space-around*/
  font-size 15px
  .tab-item
    color: #000
    flex 1
    text-align center
    height 30px
    line-height 30px
    bgcolor(#eee)
    &:hover
        bgcolor(#ddd)
    &.active
        bgcolor(#999)
</style>
