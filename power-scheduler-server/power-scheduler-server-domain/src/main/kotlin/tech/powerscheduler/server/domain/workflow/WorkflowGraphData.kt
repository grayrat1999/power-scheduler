package tech.powerscheduler.server.domain.workflow

class WorkflowGraphData : ArrayList<WorkflowGraphData.WorkflowGraphDataItem>() {
    class WorkflowGraphDataItem {
        var shape: String? = null
        var attrs: Attrs? = null
        var id: String? = null
        var zIndex: Int? = null
        var source: Source? = null
        var target: Target? = null
        var position: Position? = null
        var size: Size? = null
        var view: String? = null
        var ports: Ports? = null
        var data: Data? = null

        class Attrs {
            var line: Line? = null

            class Line {
                var stroke: String? = null
                var targetMarker: TargetMarker? = null

                class TargetMarker {
                    var name: String? = null
                    var width: Int? = null
                    var height: Int? = null
                }
            }
        }

        class Source {
            var cell: String? = null
            var port: String? = null
        }

        class Target {
            var cell: String? = null
            var port: String? = null
        }

        class Position {
            var x: Int? = null
            var y: Int? = null
        }

        class Size {
            var width: Int? = null
            var height: Int? = null
        }

        class Ports {
            var groups: Groups? = null
            var items: List<Item?>? = null

            class Groups {
                var top: GroupAttr? = null
                var right: GroupAttr? = null
                var bottom: GroupAttr? = null
                var left: GroupAttr? = null

                class GroupAttr {
                    var position: String? = null
                    var attrs: Attrs? = null

                    class Attrs {
                        var circle: Circle? = null

                        class Circle {
                            var r: Int? = null
                            var magnet: Boolean? = null
                            var stroke: String? = null
                            var strokeWidth: Int? = null
                            var fill: String? = null
                            var style: Style? = null

                            class Style {
                                var visibility: String? = null
                            }
                        }
                    }
                }
            }

            class Item {
                var group: String? = null
                var id: String? = null
            }
        }

        class Data {
            var name: String? = null
            var description: String? = null
            var processor: String? = null
            var executeParams: String? = null
            var priority: Int? = null
            var maxAttemptCnt: Int? = null
            var attemptInterval: Int? = null
            var taskMaxAttemptCnt: Int? = null
            var taskAttemptInterval: Int? = null
            var jobType: String? = null
            var scriptType: String? = null
            var executeMode: String? = null
            var workflowNodeCode: String? = null
            var workflowInstanceCode: String? = null
            var workflowNodeInstanceCode: String? = null
        }
    }
}