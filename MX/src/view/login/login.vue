<style lang="less">
  @import './login.less';
</style>

<template>
  <div class="login">
    <img src="../../assets/images/loginnew.png" style="width: 100%;height: 100%" alt="">
    <div class="login-con">
      <div style="color: white;text-shadow: rgb(0, 0, 0) 5px 2px 6px;font-family:华文行楷;font-size: 35px;text-align: center;margin-bottom: 10px">知音考场管理系统</div>
      <Card icon="log-in" title="欢迎登录" :bordered="false">
        <div class="form-con">
          <login-form @on-success-valid="handleSubmit"></login-form>
        </div>
      </Card>
    </div>
    <div class="copyRight" style="color: #fff;padding-bottom:20px">
      <div>
        © Copyright Reserved 版权所有
      </div>
      <div class="messItem" style="font-size: 16px">
        武汉创伟博科技有限公司  V 2.2.6
      </div>
      <div class="messItem" style="font-size: 16px">

      </div>
    </div>

  </div>
</template>


<script>
  import LoginForm from '_c/login-form'
  import {mapActions} from 'vuex'

  export default {
    data() {
      return {
        num: 0
      }
    },
    created(){
      this.num = Math.floor(this.AF.getRandom(1)/2)
      this.$store.commit('setTagNavList',[])
    },
    components: {
      LoginForm
    },
    methods: {
        ...mapActions([
            'handleLogin',
            'getUserInfo'
        ]),
        handleSubmit(mess) {
            // console.log('', mess);
        }
    }
  }
</script>

<style>

</style>
