<template>
  <div class="box_col" style="margin-left: 20px;">
    <Menu mode="horizontal" :theme="theme1" :active-name="activeName" ref="activeName"
          style="font-size: 48px;font-weight: bold;margin-bottom: 8px" @on-select="selectKc">
      <Menu-item v-for="item in JGList" :value="item.jgdm" :name="item.jgdm">
        {{ item.jgmc }}
      </Menu-item>
    </Menu>
    <div class="box_col_auto" :style="{height:AF.getPageHeight()-150+'px',overflowX:'hidden',flex:'unset'}">
      <Row :gutter="12">
        <Col span="8" v-for="(item,index) in list" :prop="item.zdmc" :key="item.zdId">
          <Card style="margin-top: 12px;">
            <p slot="title">
              <Icon type="ios-car"></Icon>
              {{ item.by9 }}【{{ item.by8 }}】
            </p>
            <Row>
              <Col span="20" v-if="item.zddm =='K3KF'">
                <InputNumber v-model="item.zdmc" :placeholder="'请填写练车价格'" style="width: 200px;"
                             @on-change="change(item)"></InputNumber>
                <span v-if="item.zddm =='K3PY'"> 元套餐</span>
                <span v-else-if="item.zddm =='K3KF'">元/人</span>
                <span v-else> 元套餐</span>
              </Col>
              <Col span="20" v-if="item.zddm !='K3KF'&&item.zddm !='K3PY'">
                <InputNumber v-model="item.zdmc" :placeholder="'请填写练车单价...'" style="width: 200px;"></InputNumber>
                <span v-if="item.by5=='00'">元/小时</span>
                <span v-else-if="item.by5=='10'">元/人</span>
                <span v-else> 元/人</span>
              </Col>
              <Col span="20" v-if="item.zddm =='K3PY'">
                <InputNumber v-model="item.zdmc" :placeholder="'请填写练车单价...'" style="width: 200px;"></InputNumber>
                <span>元/人</span>
              </Col>
            </Row>
            <Row style="margin-top: 16px;">
              <Col span="20">
                <InputNumber v-model="item.by3" :placeholder="'请填写练车单价...'" style="width: 200px;"></InputNumber>
                <span v-if="item.zddm =='K3PY'"> 元/人</span>
                <span v-else-if="item.zddm =='K3KF'">返点金额</span>
                <!--<span v-else> 元/分钟</span>-->
                <span v-if="item.by5=='00'">元/分钟</span>
                <span v-else-if="item.by5=='10'">返点金额</span>
                <span v-else> 元/人</span>
              </Col>
              <Col span="4">
              </Col>
            </Row>
            <Row style="margin-top: 16px;">
              <Col span="20">
                <InputNumber v-model="item.by4" :placeholder="'请填写练车单价...'" style="width: 200px;"></InputNumber>
                <span>返点率</span>
              </Col>
              <Col span="4">
                <Button type="primary" @click="confirm(item)">修改</Button>
              </Col>
            </Row>
            <Row style="margin-top: 16px;">
              <Col span="24">
                <i-switch v-model="item.by2" @on-change="confirm(item)"></i-switch>
                <span>启用【刷卡发车】</span>
              </Col>
            </Row>
            <Row style="margin-top: 16px;">
              <Col span="24">
                <i-switch v-model="item.by6" @on-change="confirm(item)"></i-switch>
                <span>启用【车辆绑卡】</span>
              </Col>
            </Row>
            <Row style="margin-top: 16px;">
              <Col span="24">
                <i-switch v-model="item.by7" @on-change="confirm(item)"></i-switch>
                <span>启用【刷卡点火】</span>
              </Col>
            </Row>
          </Card>
        </Col>
      </Row>
    </div>
  </div>
</template>

<script>
  export default {
    name: "index",
    data() {
      return {
        v: this,
        activeName: '',
        JGList: [],
        theme1: 'light',
        formItem: {},
        ruleInline: {},
        formItemGroup: [],
        list: [],
        param: {
          zdlmdm: 'ZDCLK1045',
          by1: '科三',
          orderBy: 'jgdm asc,zddm asc',
          jgdmLike: ''
        }
      }
    },
    methods: {
      selectKc(val) {
        this.param.jgdmLike = val
        this.getData()
      },
      getJgs() {
        this.$http.get("/api/lccl/getJgsByOrgCode").then(res => {
          this.JGList = res.result;
          this.param.jgdmLike = this.JGList[0].jgdm
          this.getData()
          this.activeName = this.JGList[0].jgdm
          this.$nextTick(() => {
            this.$refs.activeName.updateActiveName();
          })
        })
      },
      change(item) {
        item.by3 = parseInt(item.zdmc / 60);
      },
      confirm(item) {
        item.by2 = item.by2 ? '1' : '0'
        item.by6 = item.by6 ? '1' : '0'
        item.by7 = item.by7 ? '1' : '0'
        this.$http.post(this.apis.DICTIONARY_LIST.CHANGE, item).then((res) => {
          if (res.code == 200) {
            this.$Message.success(res.message);
            this.getData()
          } else {
            this.$Messgae.error(res.message);
          }
        })
      },
      getData() {
        this.$http.get(this.apis.DICTIONARY_LIST.list, {params: this.param}).then((res) => {
          if (res.code == 200 && res.result) {
            this.list = res.result
            for (let r of this.list) {
              r.editMode = false
              r.zdmc = parseInt(r.zdmc)
              r.by3 = parseFloat(r.by3)
              r.by4 = parseFloat(r.by4)
              r.by2 = !(r.by2 === '0' || r.by2 === '')
              r.by6 = !(r.by6 === '0' || r.by6 === '')
              r.by7 = !(r.by7 === '0' || r.by7 === '')
            }
          } else {
            this.$Message.error(res.message)
          }
        })
      }
    },
    mounted() {

    },
    created() {
      this.getJgs();
    }
  }
</script>

<style scoped>
</style>
