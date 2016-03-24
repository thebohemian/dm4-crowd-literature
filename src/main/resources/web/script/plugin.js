dm4c.add_plugin("eu.crowd-literature", function() {

    var start_page = new Topic(dm4c.restc.get_topic_by_value("uri", "crowd.omnibus.start_page", true))

    dm4c.add_listener("canvas_commands", function() {
        return [
            {
                is_separator: true,
                context: "context-menu"
            },
            {
                label: "CROWD Omnibus start page",
                handler: function() {
                    dm4c.show_topic(start_page, "show", undefined, true)     // coordinates=undefined, do_center=true
                },
                context: "context-menu"
            }
        ]
    })
})
