<template>
  <div class="box_col" style="margin-left: 20px;">
    <Row>
      <Col span="20">
        <Menu mode="horizontal" :theme="theme1" :active-name="activeName" ref="activeName"
              style="font-size: 48px;font-weight: bold;margin-bottom: 8px" @on-select="selectKc">
          <Menu-item v-for="item in JGList" :value="item.jgdm" :name="item.jgdm">
            {{ item.jgmc }}
          </Menu-item>
        </Menu>
      </Col>
      <Col span="4">
        <div style="margin-top: 5%;height:100%;background-color: white">
          <Select v-model="param.by5" clearable @on-change="getData" style="width: 100px;margin-left: 20%"
                  placeholder="请选择套餐类型">
            <Option v-for="item in tcList" :value="item.val">{{ item.label }}</Option>
          </Select>
          <Tooltip placement="top" content="新增套餐">
            <Button style="margin-left: 100%;" type="primary" icon="md-add" @click="createTc"></Button>
          </Tooltip>
        </div>
      </Col>
    </Row>
    <div class="box_col_auto" :style="{height:AF.getPageHeight()-150+'px',overflowX:'hidden',flex:'unset'}">
      <Row :gutter="12">
        <Col span="8" v-for="(item,index) in list" :prop="item.zdmc" :key="item.zdId">
          <Card style="margin-top: 12px;">
            <p slot="title">
              <Icon type="ios-car"></Icon>
              {{ item.by9 }}
            </p>
            <Form label-position="left" :label-width="150">
              <Row>
                <Col span="20" v-if="item.zddm.includes('K2KF')">
                  <FormItem label="单价:">
                    <InputNumber v-model="item.zdmc" :placeholder="'请填写练车价格'" style="width: 100px;"
                                 @on-change="change(item)"></InputNumber>
                    <span>元/人</span>
                  </FormItem>
                </Col>
                <Col span="20" v-if="item.zddm.includes('K2JS')">
                  <FormItem label="单价:">
                    <InputNumber v-model="item.zdmc" :placeholder="'请填写练车单价...'" style="width: 100px;"></InputNumber>
                    <span v-if="!item.qz">元/小时</span>
                    <span v-else>元/人</span>
                  </FormItem>
                </Col>
                <Col span="20" v-if="item.zddm.includes('K2PY')">
                  <FormItem label="单价:">
                    <InputNumber v-model="item.zdmc" :placeholder="'请填写练车单价...'" style="width: 100px;"></InputNumber>
                    <span>元/人</span>
                  </FormItem>
                </Col>
              </Row>
              <Row>
                <Col span="20" v-if="item.zddm.includes('K2JS')">
                  <FormItem label="单价(每分钟):">
                    <InputNumber v-model="item.by3" :placeholder="'请填写每分钟金额...'" readonly
                                 style="width: 100px;"></InputNumber>
                    <span> 元 </span>
                  </FormItem>
                </Col>
                <Col span="20" v-else-if="item.zddm.includes('K2KF')|| item.zddm.includes('K2PY')">
                  <FormItem label="返点金额:">
                    <InputNumber v-model="item.by3" :placeholder="'请填写返点金额'" style="width: 100px;"></InputNumber>
                    <span>元</span>
                  </FormItem>
                </Col>
                <Col span="4">
                </Col>
              </Row>
              <Row>
                <Col span="20" v-if="item.qz">
                  <FormItem label="套餐时长:">
                    <InputNumber v-model="item.qz" :placeholder="'请填写套餐时长...'" style="width: 100px;"></InputNumber>
                    <span>分钟</span>
                  </FormItem>
                </Col>
                <Col span="20" v-else>
                  <FormItem label="套餐时长:">
                    <Input value="0" style="width: 100px" readonly></Input>
                    <span>分钟</span>
                  </FormItem>
                </Col>
                <Col span="4">
                </Col>
              </Row>
              <Row>
                <Col span="20" v-if="item.zddm.includes('K2KF')">
                  <FormItem label="抵扣时长(仅开放日):">
                    <InputNumber v-model="item.by10" :placeholder="'请填写抵扣时长...'" style="width: 100px;"></InputNumber>
                    <span>分钟</span>
                  </FormItem>
                </Col>
                <Col span="20" v-else-if="item.zddm.includes('K2JS')">
                  <FormItem label="保底时长:">
                    <InputNumber v-model="item.by10" :placeholder="'请填写保底时长...'"
                                 style="width: 100px;"></InputNumber>
                    <span>分钟</span>
                  </FormItem>
                </Col>
                <Col span="20" v-else>
                  <FormItem label="抵扣时长(不可用):">
                    <InputNumber v-model="item.by10" readonly :placeholder="'抵扣时长...'"
                                 style="width: 100px;"></InputNumber>
                    <span>分钟</span>
                  </FormItem>
                </Col>
                <Col span="4">
                </Col>
              </Row>
              <Row>
                <Col span="16" v-if="item.zddm.includes('K2JS')">
                  <FormItem label="返点率:">
                    <InputNumber v-model="item.by4" :placeholder="'请填写返点率...'" style="width: 100px;"></InputNumber>
                  </FormItem>
                </Col>
                <Col span="16" v-if="item.zddm.includes('K2KF')">
                  <FormItem label="返点率:">
                    <InputNumber v-model="item.by4" :placeholder="'请填写返点率...'" style="width: 100px;"></InputNumber>
                  </FormItem>
                </Col>
                <Col span="16" v-if="item.zddm.includes('K2PY')">
                  <FormItem label="返点率:">
                    <InputNumber v-model="item.by4" :placeholder="'请填写返点率...'" readonly
                                 style="width: 100px;"></InputNumber>
                  </FormItem>
                </Col>
                <Col span="4">
                  <Button type="primary" @click="confirm(item)">修改</Button>
                </Col>
                <Col span="4">
                  <Button type="error" @click="deleteTc(item)">删除</Button>
                </Col>
              </Row>
              <Row style="margin-top: 8px;">
                <Col span="24">
                  <i-switch v-model="item.by2" @on-change="confirm(item)"></i-switch>
                  <span>启用【刷卡发车】</span>
                </Col>
              </Row>
              <Row style="margin-top: 8px;">
                <Col span="24">
                  <i-switch v-model="item.by6" @on-change="confirm(item)"></i-switch>
                  <span>启用【车辆绑卡】</span>
                </Col>
              </Row>
              <Row style="margin-top: 8px;">
                <Col span="24">
                  <i-switch v-model="item.by7" @on-change="confirm(item)"></i-switch>
                  <span>启用【刷卡点火】</span>
                </Col>
              </Row>
            </Form>
          </Card>
        </Col>
      </Row>
    </div>
    <component :is="compName" :jgdm="activeName"></component>
  </div>
</template>

<script>
import savetc from './comp/savetc'

export default {
  name: "index",
  components: {savetc},
  data() {
    return {
      activeName: '',
      JGList: [],
      theme1: 'light',
      compName: '',
      switch1: false,
      v: this,
      formItem: {},
      ruleInline: {},
      formItemGroup: [],
      list: [],
      tcList: [{val: '00', label: '计时'}, {val: '20', label: '培优'}, {val: '30', label: '开放'}],
      param: {
        zdlmdm: 'ZDCLK1045',
        by1: '科二',
        orderBy: 'zddm asc,by9 asc',
        jgdmLike: ''
      }
    }
    },
    methods: {
      deleteTc(item) {
        this.swal({
          title: '是否删除套餐' + item.by9,
          type: 'warning',
          cancelButtonText: '取消',
          confirmButtonText: '确认',
          showCancelButton: true,
          showConfirmButton: true
        }).then(p => {
          if (p.value) {
            this.$http.get('/api/lcjl/removetc/' + item.zdId).then(res => {
              if (res.code == 200) {
                this.$Message.success(res.message)
              } else {
                this.swal({
                  title: res.message,
                  type: 'error'
                })
              }
              this.getData();
            })
          }
        })
      },
      createTc() {
        this.compName = 'savetc'
      },
      selectKc(val) {
        this.param.jgdmLike = val
        this.activeName = val
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
        this.list = []
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
