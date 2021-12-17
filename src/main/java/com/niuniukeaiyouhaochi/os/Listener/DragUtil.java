package com.niuniukeaiyouhaochi.os.Listener;

import javafx.scene.Node;
import javafx.stage.Stage;

/**
 * @description:
 * @projectName:DPYos
 * @see:com.niuniukeaiyouhaochi.os.Listener
 * @author: pc
 * @createTime:2021/10/23 12:31
 * @version:1.0
 */

public class DragUtil {
	public static void addDragListener(Stage stage, Node root) {
		new DragListener(stage).enableDrag(root);
	}
}
