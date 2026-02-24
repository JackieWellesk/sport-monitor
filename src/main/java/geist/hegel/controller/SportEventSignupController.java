package geist.hegel.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import geist.hegel.common.R;
import geist.hegel.entity.SportEvent;
import geist.hegel.entity.SportEventSignup;
import geist.hegel.service.SportEventService;
import geist.hegel.service.SportEventSignupService;
import geist.hegel.utils.UserUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 赛事报名管理控制器
 * 说明：
 * 该控制器用于赛事报名信息的查询与管理。
 * 主要用于管理员/教师查看学生报名情况。
 * 数据来源：
 *  - sport_event_signup（报名表）
 *  - sys_user（学生信息）
 */
@RestController
@RequestMapping("/api/events")
public class SportEventSignupController {

    @Resource
    private SportEventSignupService signupService;

    @Resource
    private SportEventService eventService;

    /**
     * 接口说明：查询报名列表
     * 请求路径：
     *   GET /api/events/signups
     * 可扩展：
     *  - 支持分页
     */
    @GetMapping("/signups")
    public R<Object> listSignups(@ModelAttribute SportEventSignup cond) {
        return R.ok(signupService.queryPage(cond));
    }

    @DeleteMapping("/signups/{id}")
    public R<Object> deleteSignup(@PathVariable Long id) {
        SportEventSignup signup = signupService.selectById(id);
        SportEvent event = eventService.selectInfoById(signup.getEventId());
        if (event == null || !event.getStatus().equals("PUBLISHED")) {
            return R.fail("赛事已结束或关闭!");
        }
        return R.ok(signupService.deleteById(id));
    }

    /**
     * 报名逻辑
     */
    @PutMapping("/signup/me/{eventId}")
    public R<Object> mySignup(@PathVariable Long eventId) {
        // 1. 获取当前用户的ID
        Long userId = UserUtils.getUserId();
        if (userId == null) {
            return R.fail("用户未登录");
        }

        // 2. 检查赛事是否存在且已发布
        SportEvent event = eventService.selectInfoById(eventId);
        if (event == null || !event.getStatus().equals("PUBLISHED")) {
            return R.fail("赛事未找到或未开放报名");
        }

        // 3. 检查报名时间是否有效
        if (event.getSignupStart().isAfter(LocalDateTime.now()) || event.getSignupEnd().isBefore(LocalDateTime.now())) {
            return R.fail("报名时间已结束");
        }

        // 4. 检查用户是否已经报名
        if (event.getHasSignup()) {
            return R.fail("您已报名此赛事");
        }

        if (Objects.equals(event.getSignupNumber(), event.getMaxParticipants())) {
            return R.fail("该赛事人数已满");
        }

        // 5. 创建新的报名记录
        SportEventSignup newSignup = new SportEventSignup();
        newSignup.setEventId(eventId);
        newSignup.setUserId(userId);
        newSignup.setStatus("SIGNED");
        newSignup.setSignupTime(LocalDateTime.now());
        signupService.insert(newSignup);
        return R.ok("报名成功");
    }

}