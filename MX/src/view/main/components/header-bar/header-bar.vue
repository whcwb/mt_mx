<template>
  <div class="header-bar">
    <sider-trigger :collapsed="collapsed" icon="md-menu" @on-change="handleCollpasedChange"></sider-trigger>
    <!--<custom-bread-crumb class="crumb" show-icon style="margin-left: 30px;vertical-align: text-top" :list="breadCrumbList"></custom-bread-crumb>-->
    <Button :style="{transform: 'rotateZ(' + (this.shrink ? '-90' : '0') + 'deg)'}" type="text" @click="toggleClick">
      <Icon type="ios-home" size="32"></Icon>
    </Button>
    <fullscreen v-model="isFullscreen" style="margin-right: 10px;"/>
    <div class="custom-content-con">
      <slot></slot>
    </div>
  </div>
</template>
<script>
  import siderTrigger from './sider-trigger'
  import customBreadCrumb from './custom-bread-crumb'
  import './header-bar.less'
  import Fullscreen from '../fullscreen/fullscreen'

  export default {
  name: 'HeaderBar',
  components: {
    siderTrigger,
    customBreadCrumb,
      Fullscreen
  },
  props: {
      shrink: {
          type: Boolean,
          default: false
      },
    collapsed: Boolean
  },
      data(){
        return{
            isFullscreen: false
        }
      },

  computed: {
    breadCrumbList () {
      return this.$store.state.app.breadCrumbList
    }
  },
      watch:{
          isFullscreen: function(newVal, old){				//非josn用法
              if (newVal){
                  console.log(newVal);
                  this.$emit('changenewval',true);
                  // this.$parent.changenewvals(newVal)
              }else {
                  console.log(newVal);
                  this.$emit('changenewval',false);
                  // this.$parent.changenewvals(!newVal)
              }
          },
          //
          // 'isFullscreen':function (newVal) {
          //     if (newVal){
          //         console.log(newVal);
          //         this.$emit('changenewval',true);
          //     }else {
          //         console.log(newVal);
          //         this.$emit('changenewval',false);
          //     }
          // }
      },
  methods: {
    handleCollpasedChange (state) {
      this.$emit('on-coll-change', state)
    },
      toggleClick(){
          this.$router.push({
              name: 'home'
          })
      }
  }
}
</script>
<style lang="less">
</style>
