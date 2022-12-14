package cn.ztuo.bitrade.controller.promotion;

import com.alibaba.fastjson.JSONObject;
import com.querydsl.core.types.dsl.BooleanExpression;
import cn.ztuo.bitrade.annotation.AccessLog;
import cn.ztuo.bitrade.constant.AdminModule;
import cn.ztuo.bitrade.constant.PageModel;
import cn.ztuo.bitrade.constant.PromotionRewardType;
import cn.ztuo.bitrade.entity.Member;
import cn.ztuo.bitrade.entity.RewardPromotionSetting;
import cn.ztuo.bitrade.service.LocaleMessageSourceService;
import cn.ztuo.bitrade.service.MemberPromotionService;
import cn.ztuo.bitrade.service.MemberService;
import cn.ztuo.bitrade.service.RewardPromotionSettingService;
import cn.ztuo.bitrade.util.BigDecimalUtils;
import cn.ztuo.bitrade.util.ExcelUtil;
import cn.ztuo.bitrade.util.MessageResult;
import cn.ztuo.bitrade.util.PredicateUtils;
import cn.ztuo.bitrade.vo.PromotionMemberVO;
import cn.ztuo.bitrade.vo.RegisterPromotionVO;

import cn.ztuo.bitrade.entity.QMember;
import cn.ztuo.bitrade.model.MemberPromotionScreen;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("promotion/member")
public class MemberPromotionController {

    @Autowired
    private MemberService memberService ;

    @Autowired
    private MemberPromotionService memberPromotionService ;

    @Autowired
    private RewardPromotionSettingService rewardPromotionSettingService ;

    @Autowired
    private LocaleMessageSourceService messageSource;

    @PostMapping("page-query")
    @RequiresPermissions("promotion:member:page-query")
    @AccessLog(module = AdminModule.PROMOTION,operation = "????????????????????????")
    public MessageResult page(PageModel pageModel, MemberPromotionScreen screen){
        Map<String,Object> map = getMemberPromotions(pageModel,screen);
        PageImpl<Object> pageImpl = new PageImpl<>((List<Object>)map.get("list"),pageModel.getPageable(),(long)map.get("total"));
        return MessageResult.getSuccessInstance(messageSource.getMessage("SUCCESS"),pageImpl);
    }


    @RequiresPermissions("promotion:member:details")
    @PostMapping("details")
    @AccessLog(module = AdminModule.PROMOTION,operation = "??????????????????")
    public MessageResult promotionDetails(PageModel pageModel,
                                          @RequestParam("memberId")Long memberId){
        pageModel.setSort();
        Page<RegisterPromotionVO> page = memberPromotionService.getPromotionDetails(memberId,pageModel);
        return MessageResult.getSuccessInstance(messageSource.getMessage("SUCCESS"),page);
    }

    @RequiresPermissions("promotion:member:out-excel")
    @GetMapping("out-excel")
    public void outExcel(PageModel pageModel, MemberPromotionScreen screen,
                         HttpServletResponse response) throws Exception {
        Map<String,Object> map = getMemberPromotions(pageModel,screen);
        ExcelUtil.listToExcel((List<PromotionMemberVO>)map.get("list"),PromotionMemberVO.class.getDeclaredFields(),response.getOutputStream());
    }

    private Map getMemberPromotions(PageModel pageModel, MemberPromotionScreen screen){
        List<BooleanExpression> booleanExpressions = new ArrayList<>() ;
        if(!StringUtils.isEmpty(screen.getAccount())) {
            booleanExpressions.add(QMember.member.username.like("%"+screen.getAccount()+"%")
                    .or(QMember.member.realName.like("%"+screen.getAccount()+"%"))
                    .or(QMember.member.mobilePhone.like(screen.getAccount()+"%"))
                    .or(QMember.member.email.like(screen.getAccount()+"%")));
        }
        if(screen.getMinPromotionNum()!=-1) {
            booleanExpressions.add(QMember.member.firstLevel.add(QMember.member.secondLevel).goe(screen.getMinPromotionNum()));
        }

        if(screen.getMaxPromotionNum()!=-1) {
            booleanExpressions.add(QMember.member.firstLevel.add(QMember.member.secondLevel).loe(screen.getMaxPromotionNum()));
        }

        RewardPromotionSetting setting = rewardPromotionSettingService.findByType(PromotionRewardType.REGISTER);

        Assert.notNull(setting,"?????????????????? null");

        String info = setting.getInfo() ;

        JSONObject jsonObject = JSONObject.parseObject(info);

        BigDecimal one = jsonObject.getBigDecimal("one");

        BigDecimal two = jsonObject.getBigDecimal("two");

        Map<String,String> map = new HashMap<>();

        Page<Member> page = memberService.findAll(PredicateUtils.getPredicate(booleanExpressions),pageModel.getPageable());

        List<PromotionMemberVO> list = page.getContent().stream().map(x -> PromotionMemberVO.builder().id(x.getId())
                .username(x.getUsername())
                .email(x.getEmail())
                .mobilePhone(x.getMobilePhone())
                .realName(x.getRealName())
                .promotionCode(x.getPromotionCode())
                .promotionNum(x.getFirstLevel()+x.getSecondLevel())
                .reward(map.put(setting.getCoin().getName(),
                        BigDecimalUtils.mul(one,new BigDecimal(x.getFirstLevel()+"").add(
                                BigDecimalUtils.mul(two,new BigDecimal(x.getSecondLevel()+""))
                        )).toString())!=null?map:null)
                .build())
                .collect(Collectors.toList());
        Map<String,Object> map1 = new HashMap();
        map1.put("total",page.getTotalElements());
        map1.put("list",list);
        return map1 ;
    }
}
