<template>
  <div id="app">
  <div class="tab">
    <div>评论</div>
    <div>价格</div>
  </div>
    <router-view></router-view>
  </div>
</template>

<script>
</script>

<style lang="stylus" rel="stylesheet/stylus">
color=red;
vendor(prop, args)
  -webkit-{prop} args
  -moz-{prop} args
  {prop} args
pad(n)
  padding (n)

border-radius()
  vendor('border-radius', arguments)

body
  pad 5px
.tab
  vendor
  display flex
  justify-content space-around
  font-size 15px
  border-radius 8px
  border 2px solid color
</style>
