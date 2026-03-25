package com.zeta.iot.service.impl

import com.mybatisflex.spring.service.impl.ServiceImpl
import com.zeta.iot.dao.IotTelemetry10mMapper
import com.zeta.iot.model.entity.IotTelemetry10m
import com.zeta.iot.service.IIotTelemetry10mService
import org.springframework.stereotype.Service

@Service
class IotTelemetry10mServiceImpl : IIotTelemetry10mService, ServiceImpl<IotTelemetry10mMapper, IotTelemetry10m>()

