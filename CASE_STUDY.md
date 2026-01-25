# Case Study Scenarios to discuss

## Scenario 1: Cost Allocation and Tracking
**Situation**: The company needs to track and allocate costs accurately across different Warehouses and Stores. The costs include labor, inventory, transportation, and overhead expenses.

**Task**: Discuss the challenges in accurately tracking and allocating costs in a fulfillment environment. Think about what are important considerations for this, what are previous experiences that you have you could related to this problem and elaborate some questions and considerations

**Questions and considerations:**
- **Granularity and Value Objects:** We should define "Cost" as a value object within our domain models. Challenges arise in deciding the granularity—do we track at the SKU, Order, or Warehouse level? High granularity improves accuracy but increases data volume and processing overhead.
- **Allocation Keys:** How are overheads like rent and utilities distributed? Software-wise, we can implement an "Allocation Service" that uses dynamic keys (e.g., square footage or throughput volume) to split these costs automatically.
- **Event-Driven Collection:** To minimize performance impacts on the core fulfillment flow, we should use domain events (e.g., `WorkShiftCompleted`, `InventoryMoved`) to asynchronously collect cost data into a dedicated "Cost Ledger."
- **Auditability:** Every cost entry MUST be linked to a source event and timestamp to ensure full auditability, which is a non-negotiable requirement for financial compliance.

## Scenario 2: Cost Optimization Strategies
**Situation**: The company wants to identify and implement cost optimization strategies for its fulfillment operations. The goal is to reduce overall costs without compromising service quality.

**Task**: Discuss potential cost optimization strategies for fulfillment operations and expected outcomes from that. How would you identify, prioritize and implement these strategies?

**Questions and considerations:**
- **Location-Based Optimization:** Can we reduce transportation costs by dynamically re-routing `Product` flows based on which `Location` (city) has the lowest current overhead? 
- **Capacity Management:** Identifying underutilized `Warehouses` and using the "Replacement" mechanism to consolidate small hubs into larger, more efficient ones in the same `Location`.
- **Expected Outcomes:** Reduction in "Cost-per-Unit" and improved stock turn-over rates in `Stores`.
- **Prioritization:** I would use a value-stream mapping approach to identify where the most waste (time/money) occurs in the fulfillment chain.

## Scenario 3: Integration with Financial Systems
**Situation**: The Cost Control Tool needs to integrate with existing financial systems to ensure accurate and timely cost data. The integration should support real-time data synchronization and reporting.

**Task**: Discuss the importance of integrating the Cost Control Tool with financial systems. What benefits the company would have from that and how would you ensure seamless integration and data synchronization?

**Questions and considerations:**
- **Reliability:** How do we ensure that when a `Store` or `Warehouse` is created, the cost data is synced with the ERP? I would implement the Transactional Outbox pattern to guarantee that business changes and financial events are committed atomically.
- **Contract-First Integration:** Using OpenAPI specs to define the data exchange format between the Cost Control Tool and external financial systems, ensuring seamless integration.
- **Benefits:** Automated tax reporting, real-time margin analysis, and elimination of manual reconciliation errors.
- **Data Integrity:** Supporting idempotent updates so that retries in the integration layer don't result in duplicate cost entries in the financial system.

## Scenario 4: Budgeting and Forecasting
**Situation**: The company needs to develop budgeting and forecasting capabilities for its fulfillment operations. The goal is to predict future costs and allocate resources effectively.

**Task**: Discuss the importance of budgeting and forecasting in fulfillment operations and what would you take into account designing a system to support accurate budgeting and forecasting?

**Questions and considerations:**
- **Historical Analysis:** Leveraging past cost data from archived `Warehouses` (using the same BU Code history) to predict future spending patterns for similar `Locations`.
- **Scenario Modeling:** Capacity to model "What-if" scenarios, such as the impact of rising labor costs in a specific city on the overall fulfillment budget.
- **Seasonality:** Factoring in promotional peaks (Black Friday, etc.) which drastically change `Product` throughput and labor requirements.
- **Resource Allocation:** Designing the system to suggest optimal staffing levels in `Warehouses` based on forecasted `Product` demand in the linked `Stores`.

## Scenario 5: Cost Control in Warehouse Replacement
**Situation**: The company is planning to replace an existing Warehouse with a new one. The new Warehouse will reuse the Business Unit Code of the old Warehouse. The old Warehouse will be archived, but its cost history must be preserved.

**Task**: Discuss the cost control aspects of replacing a Warehouse. Why is it important to preserve cost history and how this relates to keeping the new Warehouse operation within budget?

**Questions and considerations:**
- **Continuity of Costs:** Why is it important to preserve history? Reusing the Business Unit Code implies the new `Warehouse` is a successor. We need the old data to benchmark the ROI of the replacement.
- **Audit Trails:** Ensuring that cost history from the archived unit is indexed by the same BU Code but timestamped clearly to distinguish between the "before" and "after" performance.
- **Budget Alignment:** By analyzing the previous unit's overhead, we can set realistic "Run Rate" targets for the new facility and flag anomalies early if the new setup is less efficient than expected.
- **Asset Depreciation:** Tracking how the decommissioning of old equipment in the archived unit impacts the new unit's initial budget.

## Instructions for Candidates
Before starting the case study, read the [BRIEFING.md](BRIEFING.md) to quickly understand the domain, entities, business rules, and other relevant details.

**Analyze the Scenarios**: Carefully analyze each scenario and consider the tasks provided. To make informed decisions about the project's scope and ensure valuable outcomes, what key information would you seek to gather before defining the boundaries of the work? Your goal is to bridge technical aspects with business value, bringing a high level discussion; no need to deep dive.
