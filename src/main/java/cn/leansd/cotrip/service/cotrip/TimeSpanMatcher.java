package cn.leansd.cotrip.service.cotrip;

import cn.leansd.base.types.TimeSpan;

public class TimeSpanMatcher {
    /**
     * 判断两个给定的 {@link TimeSpan} 对象是否重叠或相邻。
     *
     * <p><b>设计契约:</b></p>
     * <ul>
     *   <li>如果第一个时间段的结束时间在第二个的开始时间之前，它们不匹配。</li>
     *   <li>如果第一个时间段的开始时间在第二个的结束时间之后，它们不匹配。</li>
     *   <li>相邻的时间段被视为匹配。例如，8:00-8:30 和 8:30-9:00 被认为是匹配的。</li>
     * </ul>
     *
     * @param timeSpan1 需要匹配的第一个时间段。
     * @param timeSpan2 与第一个时间段进行比较的第二个时间段。
     * @return 如果两个时间段重叠或相邻，返回 {@code true}，否则返回 {@code false}。
     */
    public boolean match(TimeSpan timeSpan1, TimeSpan timeSpan2) {
        // 这里只是一个简单的示例，判断两个时间段是否重叠。
        // 如果有其他更复杂的匹配逻辑，您可以在这里进行调整。
        return !(timeSpan1.getEnd().isBefore(timeSpan2.getStart()) || timeSpan2.getEnd().isBefore(timeSpan1.getStart()));
    }
}
