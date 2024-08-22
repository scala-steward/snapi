/*
 * Copyright 2024 RAW Labs S.A.
 *
 * Use of this software is governed by the Business Source License
 * included in the file licenses/BSL.txt.
 *
 * As of the Change Date specified in that file, in accordance with
 * the Business Source License, use of this software will be governed
 * by the Apache License, Version 2.0, included in the file
 * licenses/APL.txt.
 */

package com.rawlabs.snapi.frontend.rql2.api

object EntryExtensionProvider {

  private val entryExtensions: Array[EntryExtension] = Array(
    new com.rawlabs.snapi.frontend.rql2.builtin.AwsV4SignedRequest,
    new com.rawlabs.snapi.frontend.rql2.builtin.FromStringBinaryEntryExtension,
    new com.rawlabs.snapi.frontend.rql2.builtin.BinaryReadEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.BinaryBase64Entry,
    new com.rawlabs.snapi.frontend.rql2.builtin.ByteFromEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.EmptyCollectionEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.BuildCollectionEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.FilterCollectionEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.AvgCollectionEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.OrderByCollectionEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.TransformCollectionEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.DistinctCollectionEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.CountCollectionEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.TupleAvgCollectionEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.MinCollectionEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.MaxCollectionEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.SumCollectionEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.FirstCollectionEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.FindFirstCollectionEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.LastCollectionEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.FindLastCollectionEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.TakeCollectionEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.ExplodeCollectionEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.UnnestCollectionEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.FromCollectionEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.GroupCollectionEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.JoinCollectionEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.InternalJoinCollectionEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.EquiJoinCollectionEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.InternalEquiJoinCollectionEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.UnionCollectionEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.ExistsCollectionEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.ContainsCollectionEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.ZipCollectionEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.MkStringCollectionEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.CsvInferAndReadEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.CsvReadEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.CsvInferAndParseEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.CsvParseEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.DateBuildEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.DateFromEpochDayEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.DateFromTimestampEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.DateParseEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.DateNowEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.DateYearEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.DateMonthEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.DateDayEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.DateSubtractEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.DateAddIntervalEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.DateSubtractIntervalEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.DecimalFromEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.DecimalRoundEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.DoubleFromEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.EnvironmentSecretEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.EnvironmentScopesEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.EnvironmentParameterEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.ErrorBuildEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.ErrorBuildWithTypeEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.ErrorGetEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.FloatFromEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.FunctionInvokeAfterEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.HttpReadEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.HttpUrlEncodeEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.HttpUrlDecodeEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.HttpPutEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.HttpDeleteEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.HttpGetEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.HttpHeadEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.HttpOptionsEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.HttpPatchEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.HttpPostEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.BuildIntervalEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.IntervalToMillisEntryExtension,
    new com.rawlabs.snapi.frontend.rql2.builtin.IntervalFromMillisEntryExtension,
    new com.rawlabs.snapi.frontend.rql2.builtin.IntervalParseEntryExtension,
    new com.rawlabs.snapi.frontend.rql2.builtin.IntervalYearsEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.IntervalMonthsEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.IntervalWeeksEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.IntervalDaysEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.IntervalHoursEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.IntervalMinutesEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.IntervalSecondsEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.IntervalMillisEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.IntFromEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.IntRangeEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.InferAndReadJsonEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.ReadJsonEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.InferAndParseJsonEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.ParseJsonEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.PrintJsonEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.KryoEncodeEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.KryoDecodeEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.EmptyListEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.BuildListEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.GetListEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.FilterListEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.TransformListEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.TakeListEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.SumListEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.MaxListEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.MinListEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.FirstListEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.FindFirstListEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.LastListEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.FindLastListEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.CountListEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.ExplodeListEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.UnnestListEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.FromListEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.UnsafeFromListEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.GroupListEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.JoinListEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.EquiJoinListEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.OrderByListEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.DistinctListEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.UnionListEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.AvgListEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.ExistsListEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.ContainsListEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.ZipListEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.MkStringListEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.LocationFromStringEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.LocationDescribeEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.LocationLsEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.LocationLlEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.LongFromEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.LongRangeEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.MathPiEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.MathRandomEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.MathPowerEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.MathAtn2Entry,
    new com.rawlabs.snapi.frontend.rql2.builtin.MathAbsEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.MathAcosEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.MathAsinEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.MathAtanEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.MathCeilingEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.MathCosEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.MathCotEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.MathDegreesEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.MathExpEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.MathLogEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.MathLog10Entry,
    new com.rawlabs.snapi.frontend.rql2.builtin.MathRadiansEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.MathSignEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.MathSinEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.MathSqrtEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.MathTanEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.MathSquareEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.MathFloorEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.MySQLInferAndReadEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.MySQLReadEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.MySQLInferAndQueryEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.MySQLQueryEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.NullableEmptyEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.NullableBuildEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.NullableIsNullEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.NullableUnsafeGetEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.NullableTransformEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.FlatMapNullableTryableEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.OracleInferAndReadEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.OracleReadEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.OracleInferAndQueryEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.OracleQueryEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.PostgreSQLInferAndReadEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.PostgreSQLReadEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.PostgreSQLInferAndQueryEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.PostgreSQLQueryEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.RecordBuildEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.RecordConcatEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.RecordFieldsEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.RecordAddFieldEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.RecordRemoveFieldEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.RecordGetFieldByIndexEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.RegexReplaceEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.RegexMatchesEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.RegexFirstMatchInEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.RegexGroupsEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.S3BuildEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.ShortFromEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.SnowflakeInferAndReadEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.SnowflakeReadEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.SnowflakeInferAndQueryEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.SnowflakeQueryEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.SQLServerInferAndReadEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.SQLServerReadEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.SQLServerInferAndQueryEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.SQLServerQueryEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.StringFromEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.StringReadEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.StringContainsEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.StringTrimEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.StringLTrimEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.StringRTrimEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.StringReplaceEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.StringReverseEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.StringReplicateEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.StringUpperEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.StringLowerEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.StringSplitEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.StringLengthEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.StringSubStringEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.StringCountSubStringEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.StringStartsWithEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.StringEmptyEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.Base64EntryExtension,
    new com.rawlabs.snapi.frontend.rql2.builtin.StringEncodeEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.StringDecodeEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.StringLevenshteinDistanceEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.StringReadLinesEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.StringCapitalizeEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.SuccessBuildEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.MandatoryExpArgsEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.MandatoryValueArgsEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.OptionalExpArgsTestEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.OptionalValueArgsTestEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.OptionalValueArgSugar,
    new com.rawlabs.snapi.frontend.rql2.builtin.VarValueArgSugarTestEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.VarExpArgsTestEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.VarValueArgsTestEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.VarNullableStringValueTestEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.VarNullableStringExpTestEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.StrictArgsTestEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.StrictArgsColPassThroughTestEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.StrictArgsColConsumeTestEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.ByteValueArgTestEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.ShortValueArgTestEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.IntValueArgTestEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.LongValueArgTestEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.FloatValueArgTestEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.DoubleValueArgTestEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.StringValueArgTestEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.BoolValueArgTestEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.DateValueArgTestEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.TimeValueArgTestEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.TimestampValueArgTestEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.IntervalValueArgTestEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.RecordValueArgTestEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.ListValueArgTestEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.TimeBuildEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.TimeParseEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.TimeNowEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.TimeHourEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.TimeMinuteEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.TimeSecondEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.TimeMillisEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.TimeSubtractEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.TimeAddIntervalEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.TimeSubtractIntervalEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.TimestampBuildEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.TimestampFromDateEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.TimestampParseEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.TimestampNowEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.TimestampRangeEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.TimestampYearEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.TimestampMonthEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.TimestampDayEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.TimestampHourEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.TimestampMinuteEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.TimestampSecondEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.TimestampMillisEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.TimestampFromUnixTimestampEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.TimestampToUnixTimestampEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.TimestampTimeBucketEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.TimestampSubtractEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.TimestampAddIntervalEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.TimestampSubtractIntervalEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.TryTransformEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.TryIsErrorEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.TryIsSuccessEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.TryFlatMapEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.TryUnsafeGetEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.TypeCastEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.TypeProtectCastEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.TypeEmptyEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.TypeMatchEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.InferAndReadXmlEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.ReadXmlEntry,
    new com.rawlabs.snapi.frontend.rql2.builtin.ParseXmlEntry
  )

  def getEntries(name: String): Array[EntryExtension] = {
    entryExtensions.filter(_.packageName == name)
  }

}
