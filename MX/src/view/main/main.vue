<template>
  <Layout style="height: 100%" class="main">
    <!--<Header>-->
<!--      <Menu mode="horizontal" theme="dark" active-name="1">-->
<!--        <div class="layout-logo"></div>-->
<!--        <div class="layout-nav" style="display: flex;flex-wrap: wrap;justify-content: space-between">-->
<!--          <MenuItem :name="item.name" v-for="(item,index) in menuList" @click.native="handleClick(item)">-->
<!--            <div style="height: 20px;text-align: center">-->
<!--              <Icon :type="item.icon"></Icon>-->
<!--            </div>-->
<!--            <div>{{ showTitleInside(item) }}</div>-->
<!--          </MenuItem>-->
<!--        </div>-->
<!--      </Menu>-->

      <!--左侧通栏-->
      <Sider style="overflow: auto" hide-trigger collapsible :width="256" :collapsed-width="64" v-model="collapsed">
        <side-menu accordion :active-name="$route.name" :collapsed="collapsed" @on-select="turnToPage"
                   :menu-list="menuList">
          <!-- 需要放在菜单上面的内容，如Logo，写在side-menu标签内部，如下 -->
          <div class="logo-con">
            <div v-show="!collapsed" style="color: white;font-size: 9pt;background-color: rgb(45, 140, 240);border-radius: 10px;padding: 10px;text-align: center">知音考场管理系统</div>

            <div v-show="collapsed" style="color: white;font-size: 9pt;background-color: rgb(45, 140, 240);border-radius: 10px;padding: 10px;text-align: center">W</div>


          </div>
        </side-menu>
      </Sider>
    <!--</Header>-->

    <Layout class="box_col">
      <div class="header-con">
        <header-bar :collapsed="collapsed" @changenewval="changenewvals" @on-coll-change="handleCollapsedChange">

          <user :user-avator="userAvator" @on-closeAll="handleCloseTag"/>
        </header-bar>
      </div>
      <div class="box_col_100">
        <div class="box_col">
<!--          <div class="tag-nav-wrapper">-->
<!--            <tags-nav :value="$route" @input="handleClick" :list="tagNavList" @on-close="handleCloseTag"/>-->
<!--            <tags-nav :value="$route" @input="handleClick" :list="menuList" @on-close="handleCloseTag"/>-->
<!--          </div>-->
          <Content class="box_col_100 content-wrapper2">
            <keep-alive :include="cacheList" style="height: 100%">
<!--              <Card style="height: 100%" :style="{height:AF.getPageHeight()-68-40-36+'px'}">-->
<!--                {{textTime}}-->

                <router-view/>
<!--              </Card>-->
            </keep-alive>
          </Content>
        </div>
      </div>
    </Layout>
  </Layout>
</template>
<script>
  import moment from 'moment'

  import {getNewTagList, getNextName, showTitle} from '@/libs/util'
  import SideMenu from './components/side-menu'
  import HeaderBar from './components/header-bar'
  import TagsNav from './components/tags-nav'
  import User from './components/user'
  import Language from './components/language'
  import {mapActions, mapMutations} from 'vuex'
  import minLogo from '@/assets/images/logo-minN.png'
  import maxLogo from '@/assets/images/logoNMax8.png'
  import './main.less'

  export default {
    name: 'Main',
    components: {
      SideMenu,
      HeaderBar,
      Language,
      TagsNav,
      User
    },
    data() {
      return {
        textTime:'',
        collapsed: false,
        minLogo,
        maxLogo,
      }
    },
    computed: {
      tagNavList() {
        return this.$store.state.app.tagNavList
      },
      tagRouter() {
        return this.$store.state.app.tagRouter
      },
      userAvator() {
        return this.$store.state.user.avatorImgPath
      },
      cacheList() {
        return this.tagNavList.length ? this.tagNavList.filter(item => !(item.meta && item.meta.notCache)).map(item => item.name) : []
      },
      menuList() {
        return this.$store.getters.menuList
        // return []
        // return JSON.parse(localStorage.getItem('menuList'))
      },
      menuArr(){
        return this.$store.state.app.permissionMenuList
      },
      local() {
        return this.$store.state.app.local
      }
    },
    created(){
      this.textTime = moment().format('YYYY年MM-DD')
      if(this.menuArr.length == 0){
        this.setPermissionMenuList(JSON.parse(localStorage.getItem('menuList')))
        // this.menuList()
      }
    },
    methods: {
      ...mapMutations([
        'setBreadCrumb',
        'setTagNavList',
        'addTag',
        'setLocal',
        'setPermissionMenuList'
      ]),
      ...mapActions([
        'handleLogin'
      ]),
      turnToPage(name) {
        if (name.indexOf('isTurnByHref_') > -1) {
          window.open(name.split('_')[1])
          return
        }
        this.$router.push({
          name: name
        })
      },
        changenewvals(a){
            this.collapsed = a
        },
      handleCollapsedChange(state) {
        this.collapsed = state
      },
      showTitleInside (item) {
        return showTitle(item, this)
      },
      handleCloseTag(res, type, name) {
          //2019年10月14日。关闭异常界面，清理缓存值
          if (name == "Student-exception"){
            sessionStorage.removeItem("queryExpCode");
            sessionStorage.removeItem("queryExpKskm");
          }
        const nextName = getNextName(this.tagNavList, name)
        this.setTagNavList(res)
        if (type === 'all') this.turnToPage('home')
        else if (this.$route.name === name) this.$router.push({name: nextName})
      },
      handleClick(item) {
        this.turnToPage(item.name)
      }
    },
    watch: {
      '$route'(newRoute) {
        this.setBreadCrumb(newRoute.matched)
        this.setTagNavList(getNewTagList(this.tagNavList, newRoute))
      },
        'collapsed':function (newval) {
            console.log(newval);
        }
    },
    mounted() {
      /**
       * @description 初始化设置面包屑导航和标签导航
       */
      this.setTagNavList()
      this.addTag(this.$store.state.app.homeRoute)
      this.setBreadCrumb(this.$route.matched)
      // 设置初始语言
      this.setLocal(this.$i18n.locale)
      // 文档提示
      // this.$Notice.info({
      //   title: '想快速上手，去看文档吧',
      //   duration: 0,
      //   render: (h) => {
      //     return h('p', {
      //       style: {
      //         fontSize: '13px'
      //       }
      //     }, [
      //       '点击',
      //       h('a', {
      //         attrs: {
      //           href: 'https://lison16.github.io/iview-admin-doc/#/',
      //           target: '_blank'
      //         }
      //       }, 'iview-admin2.0文档'),
      //       '快速查看'
      //     ])
      //   }
      // })
    }
  }
</script>

<style scoped>
  .ivu-menu-dark{
    background: rgb(81, 90, 110);
  }

  .ivu-layout-header{
    padding: 0;
  }

  .ivu-menu-horizontal{
    height: auto;
  }

  .ivu-menu-horizontal .ivu-menu-item, .ivu-menu-horizontal .ivu-menu-submenu{
    padding: 0 5px;
  }
</style>
