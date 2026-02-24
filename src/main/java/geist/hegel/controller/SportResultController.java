package geist.hegel.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import geist.hegel.common.R;
import geist.hegel.entity.SportEvent;
import geist.hegel.entity.SportEventResult;
import geist.hegel.entity.SportEventSignup;
import geist.hegel.service.SportEventResultService;
import geist.hegel.service.SportEventService;
import geist.hegel.service.SportEventSignupService;
import geist.hegel.utils.UserUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/results")
public class SportResultController {

    @Resource
    private SportEventResultService resultService;

    @Resource
    private SportEventService eventService;

    @Resource
    private SportEventSignupService signupService;

    /**
     * 成绩分页列表
     * GET /api/event/result/list?current=1&size=10&eventId=&userId=
     */
    @GetMapping
    public R<Page<SportEventResult>> pageList(@ModelAttribute SportEventResult cond) {
        return R.ok(resultService.queryPage(cond));
    }

    /**
     * 成绩详情（登记页回显）
     * GET /api/event/result/{id}
     */
    @GetMapping("/{id}")
    public R<SportEventResult> detail(@PathVariable("id") Long id) {
        return R.ok(resultService.selectById(id));
    }

    /**
     * 登记成绩（新增）
     * POST /api/event/result
     */
    @PostMapping
    public R<?> create(@RequestBody SportEventResult entity) {
        // 0. 基础参数校验
        if (entity == null || entity.getEventId() == null || entity.getUserId() == null) {
            return R.fail("参数错误：eventId/userId 必填");
        }

        // 1. 权限 + 赛事状态校验（教师只能操作自己负责的赛事）
        SportEvent event = eventService.selectById(entity.getEventId());
        R<?> eventCheck = checkTeacherEventWritable(event);
        if (eventCheck != null) return eventCheck;

        // 2. 学生是否报名（且报名有效）
        SportEventSignup signup = signupService.selectOne(
                new EntityWrapper<SportEventSignup>()
                        .eq("event_id", entity.getEventId())
                        .eq("user_id", entity.getUserId())
                        .eq("status", "SIGNED") // ✅ 按你实际状态值改：SIGNED/1/0...
        );
        if (signup == null) {
            return R.fail("该学生未报名或报名已取消，不能登记成绩");
        }

        // 3. 防重复：该学生在该赛事是否已有成绩
        SportEventResult exist = resultService.selectOne(
                new EntityWrapper<SportEventResult>()
                        .eq("event_id", entity.getEventId())
                        .eq("user_id", entity.getUserId())
        );
        if (exist != null) {
            return R.fail("该学生成绩已存在，请使用修改功能");
        }

        // 4. 成绩合法性校验（按你的业务调整）
        R<?> scoreCheck = checkScoreValid(entity, null);
        if (scoreCheck != null) return scoreCheck;

        // 5. 名次唯一（如果你有 rank 且需要唯一）
        R<?> rankCheck = checkRankUnique(entity.getEventId(), entity.getRankNo(), null);
        if (rankCheck != null) return rankCheck;

        return R.ok(resultService.insert(entity));
    }

    /**
     * 修改成绩（必须带 id）
     * PUT /api/event/result/{id}
     */
    @PutMapping()
    public R<?> update(@RequestBody SportEventResult entity) {
        // 0. 基础参数校验
        if (entity == null || entity.getId() == null) {
            return R.fail("参数错误：id 必填");
        }

        // 1. 成绩必须存在
        SportEventResult db = resultService.selectById(entity.getId());
        if (db == null) {
            return R.fail("成绩不存在");
        }

        // 2. 防止篡改：eventId/userId 不允许改（强烈建议）
        // 如果你允许改，就需要更复杂的校验（不建议）
        entity.setEventId(db.getEventId());
        entity.setUserId(db.getUserId());

        // 3. 权限 + 赛事状态校验（教师只能操作自己负责的赛事）
        SportEvent event = eventService.selectById(db.getEventId());
        R<?> eventCheck = checkTeacherEventWritable(event);
        if (eventCheck != null) return eventCheck;

        // 4. 学生是否报名（建议修改也校验，避免取消报名后还能改）
        SportEventSignup signup = signupService.selectOne(
                new EntityWrapper<SportEventSignup>()
                        .eq("event_id", db.getEventId())
                        .eq("user_id", db.getUserId())
                        .eq("status", "SIGNED") // ✅ 按你实际状态值改
        );
        if (signup == null) {
            return R.fail("该学生未报名或报名已取消，不能修改成绩");
        }

        // 5. 成绩合法性校验
        R<?> scoreCheck = checkScoreValid(entity, db);
        if (scoreCheck != null) return scoreCheck;

        // 6. 名次唯一（排除自己）
        R<?> rankCheck = checkRankUnique(db.getEventId(), entity.getRankNo(), db.getUserId());
        if (rankCheck != null) return rankCheck;


        return R.ok(resultService.updateById(entity));
    }

    /**
     * 删除成绩
     * DELETE /api/event/result/{id}
     */
    @DeleteMapping("/{id}")
    public R<?> delete(@PathVariable("id") Long id) {
        SportEventResult result = resultService.selectById(id);
        Long eventId = result.getEventId();
        SportEvent event = eventService.selectById(eventId);
        if (event.getStatus().equals("FINISHED")) {
            return R.fail("该赛事已经完成，没法删除成绩");
        }
        return R.ok(resultService.deleteById(id));
    }

    /**
     * 教师是否可写该赛事成绩
     * @param event 赛事
     */
    private R<?> checkTeacherEventWritable(SportEvent event) {
        if (event == null) return R.fail("赛事不存在");

        Long teacherId = UserUtils.getUserId();
        if (teacherId == null) return R.fail("未登录");

        // 1) 是否是该赛事负责人（按你字段改）
        if (event.getCreatedBy() == null || !event.getCreatedBy().equals(teacherId)) {
            return R.fail("无权限操作该赛事成绩");
        }


        if ("FINISHED".equals(event.getStatus())) {
            return R.fail("比赛已经登记结束，不能登记成绩");
        }
        // 2) 比赛状态必须已结束（按你状态值改）
        // 例如：FINISHED / END / 2 ...
        if (!"CLOSED".equals(event.getStatus())) {
            return R.fail("比赛未结束，不能登记/修改成绩");
        }

        return null;
    }

    /**
     * 成绩合法性校验（按你的业务调整：分数制/名次制/用时制）
     */
    private R<?> checkScoreValid(SportEventResult entity, SportEventResult db) {

        // 示例：score 必填，0-100
        if (entity.getScoreValue() == null) {
            return R.fail("分数不能为空");
        }
        if (entity.getScoreValue().intValue() < 0) {
            return R.fail("分数不能为负数");
        }

        // 如果是用时：比如 timeSeconds >= 0
        // if (entity.getTimeSeconds() == null || entity.getTimeSeconds() < 0) return R.fail("用时不合法");

        // 如果是名次：rank >= 1
        if (entity.getRankNo() != null && entity.getRankNo() <= 0) {
            return R.fail("名次必须大于0");
        }

        return null;
    }

    /**
     * 名次唯一校验（如果 rank 为空就跳过）
     */
    private R<?> checkRankUnique(Long eventId, Integer rankNo, Long excludeId) {
        if (rankNo == null) return null; // 没有名次就不校验

        EntityWrapper<SportEventResult> ew = new EntityWrapper<SportEventResult>();
        ew.eq("event_id", eventId);
        ew.eq("rank_no", rankNo);

        if (excludeId != null) {
            ew.ne("user_id", excludeId);
        }

        SportEventResult sameRank = resultService.selectOne(ew);
        if (sameRank != null) {
            return R.fail("该名次已被占用");
        }
        return null;
    }

}
