package com.hotelbooking.hotel_reservation_eu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hotelbooking.hotel_reservation_eu.dto.RoomTypeBranchMappingDto;
import com.hotelbooking.hotel_reservation_eu.model.RoomTypeBranchMapping;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 房型分店关联Mapper接口
 */
@Mapper
public interface RoomTypeBranchMappingMapper extends BaseMapper<RoomTypeBranchMapping> {

    /**
     * 分页查询房型分店关联信息（包含关联数据）
     */
    @Select({
        "SELECT m.id, m.room_type_id, m.hotel_branch_id, m.is_active,",
        "       m.branch_specific_price, m.available_rooms_count, m.max_rooms_count,",
        "       m.sort_order, m.special_amenities, m.notes,",
        "       m.created_at, m.updated_at,",
        "       rt.type_name as room_type_name, rt.base_price as room_type_base_price,",
        "       rt.description as room_type_description, rt.bed_type, rt.size, rt.max_guests,",
        "       hb.branch_name as hotel_branch_name, hb.city as hotel_branch_city,",
        "       hb.country as hotel_branch_country",
        "FROM room_type_branch_mapping m",
        "LEFT JOIN room_type rt ON m.room_type_id = rt.id",
        "LEFT JOIN hotel_branch hb ON m.hotel_branch_id = hb.id",
        "ORDER BY m.id DESC"
    })
    IPage<RoomTypeBranchMappingDto> selectMappingPageWithDetails(Page<RoomTypeBranchMappingDto> page);

    /**
     * 根据分店ID查询房型列表
     */
    @Select({
        "SELECT m.id, m.room_type_id, m.hotel_branch_id, m.is_active,",
        "       m.branch_specific_price, m.available_rooms_count, m.max_rooms_count,",
        "       m.sort_order, m.special_amenities, m.notes,",
        "       rt.type_name as room_type_name, rt.base_price as room_type_base_price,",
        "       rt.description as room_type_description, rt.bed_type, rt.size, rt.max_guests",
        "FROM room_type_branch_mapping m",
        "LEFT JOIN room_type rt ON m.room_type_id = rt.id",
        "WHERE m.hotel_branch_id = #{hotelBranchId}",
        "  AND m.is_active = 1",
        "  AND rt.status = 1",
        "ORDER BY m.sort_order, rt.type_name"
    })
    List<RoomTypeBranchMappingDto> selectRoomTypesByBranchId(@Param("hotelBranchId") Long hotelBranchId);

    /**
     * 根据房型ID查询分店列表
     */
    @Select({
        "SELECT m.id, m.room_type_id, m.hotel_branch_id, m.is_active,",
        "       m.branch_specific_price, m.available_rooms_count, m.max_rooms_count,",
        "       m.sort_order, m.special_amenities, m.notes,",
        "       hb.branch_name as hotel_branch_name, hb.city as hotel_branch_city,",
        "       hb.country as hotel_branch_country",
        "FROM room_type_branch_mapping m",
        "LEFT JOIN hotel_branch hb ON m.hotel_branch_id = hb.id",
        "WHERE m.room_type_id = #{roomTypeId}",
        "  AND m.is_active = 1",
        "  AND hb.status = 1",
        "ORDER BY hb.branch_name"
    })
    List<RoomTypeBranchMappingDto> selectBranchesByRoomTypeId(@Param("roomTypeId") Long roomTypeId);

    /**
     * 检查房型和分店的关联关系是否已存在
     */
    @Select({
        "SELECT COUNT(*) FROM room_type_branch_mapping",
        "WHERE room_type_id = #{roomTypeId}",
        "  AND hotel_branch_id = #{hotelBranchId}"
    })
    int countExistingMapping(
        @Param("roomTypeId") Long roomTypeId,
        @Param("hotelBranchId") Long hotelBranchId
    );

    /**
     * 检查房型和分店的关联关系是否已存在（排除指定ID）
     */
    @Select({
        "SELECT COUNT(*) FROM room_type_branch_mapping",
        "WHERE room_type_id = #{roomTypeId}",
        "  AND hotel_branch_id = #{hotelBranchId}",
        "  AND id != #{excludeId}"
    })
    int countExistingMappingExcludeId(
        @Param("roomTypeId") Long roomTypeId,
        @Param("hotelBranchId") Long hotelBranchId,
        @Param("excludeId") Long excludeId
    );

    /**
     * 根据分店ID查询房源信息（用于客户端搜索）
     */
    @Select({
        "SELECT m.id, m.room_type_id, m.hotel_branch_id, m.is_active,",
        "       m.branch_specific_price, m.available_rooms_count, m.max_rooms_count,",
        "       m.sort_order, m.special_amenities, m.notes,",
        "       rt.type_name as room_type_name, rt.base_price as room_type_base_price,",
        "       rt.description as room_type_description, rt.bed_type, rt.size, rt.max_guests,",
        "       rt.image_url, rt.amenities,",
        "       hb.branch_name as hotel_branch_name, hb.city as hotel_branch_city,",
        "       hb.country as hotel_branch_country",
        "FROM room_type_branch_mapping m",
        "LEFT JOIN room_type rt ON m.room_type_id = rt.id",
        "LEFT JOIN hotel_branch hb ON m.hotel_branch_id = hb.id",
        "WHERE m.hotel_branch_id = #{branchId}",
        "  AND m.is_active = 1",
        "  AND rt.status = 1",
        "  AND hb.status = 1",
        "  AND m.available_rooms_count > 0",
        "ORDER BY m.sort_order, rt.base_price ASC"
    })
    List<RoomTypeBranchMappingDto> selectByBranchId(@Param("branchId") Long branchId);

    /**
     * 根据ID获取详细信息
     */
    @Select({
        "SELECT m.id, m.room_type_id, m.hotel_branch_id, m.is_active,",
        "       m.branch_specific_price, m.available_rooms_count, m.max_rooms_count,",
        "       m.sort_order, m.special_amenities, m.notes,",
        "       m.created_at, m.updated_at, m.created_by, m.updated_by,",
        "       rt.type_name as room_type_name, rt.base_price as room_type_base_price,",
        "       rt.description as room_type_description, rt.bed_type, rt.size, rt.max_guests,",
        "       hb.branch_name as hotel_branch_name, hb.city as hotel_branch_city,",
        "       hb.country as hotel_branch_country",
        "FROM room_type_branch_mapping m",
        "LEFT JOIN room_type rt ON m.room_type_id = rt.id",
        "LEFT JOIN hotel_branch hb ON m.hotel_branch_id = hb.id",
        "WHERE m.id = #{id}"
    })
    RoomTypeBranchMappingDto selectByIdWithDetails(@Param("id") Long id);
}