package im.zhaojun.test;
import im.zhaojun.zfile.ZfileApplication;
import im.zhaojun.zfile.module.inode.addition_module.textClassify.messagequeue.consumer.ClassifyReceiveConsumer;
import im.zhaojun.zfile.module.inode.addition_module.textClassify.messagequeue.producer.TextSendProducer;
import im.zhaojun.zfile.module.inode.addition_module.textClassify.model.messagequeue.send.ClassifyMessageSend;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import javax.annotation.Resource;


@SpringBootTest(classes = ZfileApplication.class)
@DirtiesContext
public class RabbitMqTest {
    @Resource
    private TextSendProducer textSendProducer;
    @Resource
    private ClassifyReceiveConsumer classifyReceiveConsumer;
    @Test
    public void tspTest(){
        String message = "尊敬的导师、亲爱的同学们：\n" +
                "\n" +
                "随着大学四年的时光飞逝，他（她）即将迎来毕业的时刻。回首这段美好的学业时光，他（她）深感对这片校园的眷恋，对学业的坚持，以及对成长过程的深刻领悟。在即将开启新的人生篇章之际，他（她）希望与大家分享自己在校内项目、比赛和志愿服务中的一些经历，展示个人的成长与感悟。\n" +
                "\n" +
                "在学术方面，他（她）或许没有取得耀眼的成绩单，但一直以来都在不懈追求知识的深度和广度。通过积极参与校内项目和比赛，他（她）不仅接触到了多领域的知识，还培养了跨学科的思维方式。尽管未有发表专业论文的经历，但通过项目合作和比赛挑战，他（她）逐渐领悟到学术的魅力和实践的重要性。成长的过程或许不能仅以分数来衡量，更应看重对知识的热爱和对问题的持续追求。\n" +
                "\n" +
                "在实际方面，尽管他（她）未有实习经验，但积极参与了多个校内项目和比赛。在一个创新设计项目中，他（她）扮演了团队的关键角色，通过与团队成员的协作，成功提出并实施了一个富有创意的解决方案。这个项目不仅锻炼了团队协作能力，也让他（她）亲身体验了实际问题解决的过程。尽管缺少实习经验，但这些项目让他（她）具备了实际操作和问题解决的能力。\n" +
                "\n" +
                "在学生干部方面，他（她）虽未曾担任过职务，但在校内项目和比赛中担任了组织者和协调者的角色。通过组织一个校内比赛，他（她）学到了团队协作、活动策划和时间管理等实际技能。虽未曾拥有学生干部的光环，但这些经历让他（她）体验到了领导团队的挑战和乐趣。\n" +
                "\n" +
                "在社会服务方面，他（她）虽然只在校内做过志愿者，但这并未妨碍他（她）对社会的关切和责任感。通过参与志愿服务，他（她）了解到社会的多样性和需要，明白了每一个小小的贡献都能为他人带来实实在在的帮助。尽管规模不大，但他（她）深知积小成大，每一个微小的行动都能在社会中产生积极的影响。\n" +
                "\n" +
                "在这里，他（她）要感谢所有支持过他（她）的人，包括家人、老师和朋友。感谢他们的鼓励和指导，是他们的支持让他（她）能够不断超越自我，迎接新的挑战。\n" +
                "\n" +
                "未来，他（她）将怀揣着对专业的热爱和对社会的责任感，踏上新的征程。他（她）期待在未来的道路上，继续努力学习，不断挑战自己，为社会创造更大的价值。\n" +
                "\n" +
                "谢谢大家！";
        ClassifyMessageSend classifyMessageSend = new ClassifyMessageSend();
        classifyMessageSend.setText(message);
        textSendProducer.sendMessage(classifyMessageSend);
    }



}
