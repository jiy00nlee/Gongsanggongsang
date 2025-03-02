package com.parasol.userapp.ui.main.reservation

import com.parasol.userapp.R


data class CategoryData(val iconID: Int, val name: String, var clicked : Boolean = false)

data class CategoryResourceItem(val drawableID: Int, var clicked: Boolean)

enum class CategoryResources(val drawableID: Int){
    MENTORING_ROOM(R.drawable.ic_mentoringroom), COMPUTER(R.drawable.ic_computer), OFFICE(R.drawable.ic_office),
    MEETING_ROOM(R.drawable.ic_meeting_room), CAFETERIA(R.drawable.ic_cafeteria), ROUNGE(R.drawable.ic_rounge),
    SHOWER_ROOM(R.drawable.ic_shower_room), WASHER(R.drawable.ic_washer), DRYER(R.drawable.ic_dryer), INDUCTION(R.drawable.ic_induction);
    companion object{
        private val categoryResourcesIdList : List<Int> = listOf(MENTORING_ROOM.drawableID, COMPUTER.drawableID, OFFICE.drawableID,
            MEETING_ROOM.drawableID, CAFETERIA.drawableID, ROUNGE.drawableID, SHOWER_ROOM.drawableID, WASHER.drawableID,
            DRYER.drawableID, INDUCTION.drawableID)
        fun makeListToClass() : List<CategoryResourceItem> {
            val rvList : MutableList<CategoryResourceItem> = mutableListOf()
            categoryResourcesIdList.forEach { rvList.add(CategoryResourceItem(it, false)) }
            return rvList }
        fun makeDrawableIDToEnumData(drawableID: Int) : Enum<CategoryResources>{
            return values().first { it.drawableID == drawableID} }
        fun makeDrawableIDToString(drawableID: Int) : String {
            return when (values().first { it.drawableID == drawableID}){
                MENTORING_ROOM -> "MENTORING_ROOM"
                COMPUTER -> "COMPUTER"
                OFFICE -> "OFFICE"
                MEETING_ROOM -> "MEETING_ROOM"
                CAFETERIA -> "CAFETERIA"
                ROUNGE -> "ROUNGE"
                SHOWER_ROOM -> "SHOWER_ROOM"
                WASHER -> "WASHER"
                DRYER-> "DRYER"
                INDUCTION -> "INDUCTION"
                else -> "MENTORING_ROOM"
            }
        }
        fun makeIconStringToEnumClass(iconString : String) : CategoryResources {
            return when (iconString){
                "MENTORING_ROOM" -> MENTORING_ROOM
                "COMPUTER" -> COMPUTER
                "OFFICE" -> OFFICE
                "MEETING_ROOM" -> MEETING_ROOM
                "CAFETERIA" -> CAFETERIA
                "ROUNGE" -> ROUNGE
                "SHOWER_ROOM" -> SHOWER_ROOM
                "WASHER" -> WASHER
                "DRYER" -> DRYER
                "INDUCTION" -> INDUCTION
                else -> MENTORING_ROOM
            }
        }
        fun makeIconStringToDrawableID(iconString: String) : Int {
            return makeIconStringToEnumClass(iconString).drawableID
        }
    }
}