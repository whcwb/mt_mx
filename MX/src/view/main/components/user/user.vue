<style lang="less">
  .timeSty{
    .time{
      color: #fa541c;
      font-size: 24px;
      line-height: 20px;
    }
  }
</style>
<template>
  <div class="user-avator-dropdown">
    <!--<Dropdown @on-click="handleClick">-->
    <!--<Avatar :src="userAvator"/>-->
    <!--<Icon :size="18" type="md-arrow-dropdown"></Icon>-->
    <!--<DropdownMenu slot="list">-->
    <!--<DropdownItem name="logout">退出登录</DropdownItem>-->
    <!--</DropdownMenu>-->
    <!--</Dropdown>-->
    <!--<Avatar :src="userAvator"/>-->
    <Button type="text"
            style="font-size:20px;font-weight: 700">
      欢迎 <span style="color: #2db7f5;">{{usermess.xm}}</span>
    </Button>
<!--    <Button type="text" class="timeSty">-->
<!--      <div class="time">-->
<!--        {{todayTime}}-->
<!--      </div>-->
<!--      &lt;!&ndash;<div class="time">&ndash;&gt;-->
<!--        &lt;!&ndash;{{todayTime_N}}&ndash;&gt;-->
<!--      &lt;!&ndash;</div>&ndash;&gt;-->
<!--    </Button>-->
<!--    <Tooltip content="全屏" style="margin-right: 16px">-->
<!--      <Button type="info"-->
<!--              size="small"-->
<!--              @click="isqp">-->
<!--        <Icon type="ios-albums-outline" size="24" color="#fff"/>-->
<!--      </Button>-->
<!--    </Tooltip>-->
<!--    <Tooltip content="UKey驱动下载" style="margin-right: 16px">-->
<!--      <Button type="warning"-->
<!--              size="small"-->
<!--              @click="ukey">-->
<!--        <Icon type="md-key" size="24" color="#fff"/>-->
<!--      </Button>-->
<!--    </Tooltip>-->
    <!--<Tooltip content="打印机驱动下载" style="margin-right: 16px">-->
      <!--<Button type="warning"-->
              <!--size="small"-->
              <!--@click="dyxz">-->
        <!--<Icon type="md-print" size="24" color="#fff"/>-->
      <!--</Button>-->
    <!--</Tooltip>-->
    <Tooltip content="身份证读卡驱动下载" style="margin-right: 16px">
      <Button type="warning"
              size="small"
              @click="xzhrsp">
        <Icon type="ios-card-outline" size="24" color="#fff"/>
      </Button>
    </Tooltip>
    <Tooltip content="驱动下载" style="margin-right: 16px">
      <Button type="warning"
              size="small"
              @click="xzhs">
        <Icon type="ios-card-outline" size="24" color="#fff"/>
      </Button>
    </Tooltip>
    <Tooltip content="读卡器驱动(练车)" style="margin-right: 16px">
      <Button type="warning"
              size="small"
              @click="xz">
        <Icon type="ios-card-outline" size="24" color="#fff"/>
      </Button>
    </Tooltip>
<!--    <Tooltip content="读卡器驱动(身份证)" style="margin-right: 16px">-->
<!--      <Button type="warning"-->
<!--              size="small"-->
<!--              @click="xzx">-->
<!--        <Icon type="ios-card" size="24" color="#fff"/>-->
<!--      </Button>-->
<!--    </Tooltip>-->
    <Tooltip content="密码修改" style="margin-right: 16px">
      <Button type="info"
              size="small"
              @click="handleClick('password')">
        <Icon type="md-person" size="24" color="#fff"/>
      </Button>
    </Tooltip>
    <Tooltip content="退出登录">
      <Button type="error"
              size="small"
              style="padding: 4px 10px;"
              @click="handleClick('logout')">
        <Icon type="md-log-out" size="18" color="#fff"/>
      </Button>
    </Tooltip>
    <component :is="compName"></component>
  </div>
</template>

<script>
  import './user.less'
  import {mapActions} from 'vuex'
  import pw from '../../../../components/passworld'
  import chineseLunar from 'chinese-lunar'
  export default {
    name: 'User',
    components: {
      pw
    },
    data() {
      return {
        todayTime:'',
        todayTime_N:'',
        compName: '',
        usermess:{},
          qp:false
      }
    },
    props: {
      userAvator: {
        type: String,
        default: ''
      }
    },
    created(){
      // this.AF.supDate()
      this.todate()
      this.usermess = JSON.parse(sessionStorage.getItem('userInfo'))
      if(!this.usermess){
        this.$router.push({
          name: 'login'
        })
      }
      this.get()
    },
    methods: {
      ukey(){
        window.open('../ukey.exe', '_blank');
      },
      dyxz(){
        window.open('../浩顺打印机驱动.zip', '_blank');
      },
      xz(){
        window.open('../读卡器驱动.zip', '_blank');
      },
      xzx(){
        window.open('../SynCardReader.exe', '_blank');
      },
      xzhs(){
        window.open('../驱动.exe', '_blank');
      },
      xzhrsp(){
        window.open('../Hsrp.exe', '_blank');
      },
      ...mapActions([
        'handleLogOut'
      ]),
        isqp(){
          if (this.qp){
              this.qitqp()
              this.qp = false
          }else {
              this.qpqp()
              this.qp = true
          }
        },
        qpqp(){
            //全屏

            var docElm = document.documentElement;
            // W3C

            if(docElm.requestFullscreen) { docElm.requestFullscreen(); }
            // FireFox

            else if(docElm.mozRequestFullScreen) { docElm.mozRequestFullScreen(); }
            // Chrome等

            else if(docElm.webkitRequestFullScreen) { docElm.webkitRequestFullScreen(); }
            // IE11

            else if(elem.msRequestFullscreen) { elem.msRequestFullscreen(); }
        },
        qitqp(){
            //W3C
            if (document.exitFullscreen) { document.exitFullscreen(); }
            // FireFox
            else if (document.mozCancelFullScreen) { document.mozCancelFullScreen(); }
            // Chrome等
            else if (document.webkitCancelFullScreen) { document.webkitCancelFullScreen(); }
            // IE11
            else if (document.msExitFullscreen) { document.msExitFullscreen(); }
        },
      get(){
        var a = 24*60*60*1000
        var b = Date.parse(new Date())
        var solarToLunar = chineseLunar.solarToLunar(new Date(b-a))
        // var format = chineseLunar.format(solarToLunar,'T'+'---'+'AYMD');
        var format = chineseLunar.format(solarToLunar,'YMD');
        // console.log(format)
        this.todayTime_N = format
      },
      todate(){
        setInterval(()=>{
          this.todayTime = this.AF.getTime()
        },1000)
      },
      handleClick(name) {
        switch (name) {
          case 'logout':
            let res = [
              {
                meta:{
                  hideInMenu:true,
                  notCache:true,
                  title:"工作台"
                },
                name:"home",
                path:"/home",
                to:"/name"
              }
            ]
            this.$emit('on-closeAll', res, 'all')
            this.$router.push({
              name: 'login'
            })
            break;
          case 'password':
            this.compName = 'pw'
            break;
        }
      }
    }
  }
</script>
