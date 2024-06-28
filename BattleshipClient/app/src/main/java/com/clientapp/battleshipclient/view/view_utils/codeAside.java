package com.clientapp.battleshipclient.view.view_utils;

public class codeAside {

    //
//    public void setShipsOnTouchListeners() {
//        for (Map.Entry<Integer, ImageView> entry : shipViewsCollection.entrySet()) {
//            ImageView shipView = entry.getValue();
//            shipView.setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//                    switch (event.getAction()) {
//                        case MotionEvent.ACTION_DOWN:
//                            int shipId = entry.getKey();
//                            Ship ship = shipCollection.get(shipId);
//                            shipView.setTag(ship); // Make sure the ship object is set as tag if not set elsewhere
//                            Log.d("myDEBUG PlacementActivity", "setShipsOnTouchListeners shipId: " + shipId);
//                            Log.d("myDEBUG PlacementActivity", "setShipsOnTouchListeners shipPositionsArray: " + ship.getShipPositionsArray());
//
//                            ClipData.Item item = new ClipData.Item(String.valueOf(shipId));
//                            String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};
//                            ClipData dragData = new ClipData("Ship ID", mimeTypes, item);
//                            if (dragData == null) {
//                                break;
//                            }
//                            CustomDragShadowBuilder shipShadow = new CustomDragShadowBuilder(v);
//                            boolean isDragging = v.startDragAndDrop(dragData, shipShadow, ship, 0);
//                            if (!isDragging) {
//                                tryOrientationChange(ship, (ImageView) v);
//                                break;
//                            }
//                            v.setVisibility(View.INVISIBLE); // Temporarily hide the view while dragging
//                        case MotionEvent.ACTION_UP:
//                            break;
//                        case MotionEvent.ACTION_MOVE:
//                            break;
//
//                        default:
//                            break;
//                    }
//                    return false;
//                }
//
//            });
//        }
//    }// end of setShipsLongClickListeners

//    private void tryOrientationChange(Ship ship, ImageView shipView) {
//        boolean isChanged = placementLogic.orientationChangeLogic(ship);
//        if (isChanged) {
//            PlacementUtils.shipViewSetOrientationAccordingToData(shipView, ship);
//            setShipViewOnGrid(ship);
//        } else {
//            //log no orientation change
//            Log.d("myDEBUG", "setShipsOnClickOrientationChange: " + "no orientation change");
//            displayMessageForShortTime(ClientMessages.TRY_ANOTHER_PLACE);
////                    Toast.makeText(this, "Try on another location", Toast.LENGTH_SHORT).show();
//        }
//    }
}
