/**
 * Autogenerated by Thrift Compiler (0.10.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package net.morimekta.test.thrift.serialization.deep;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
public class LevelTree implements org.apache.thrift.TBase<LevelTree, LevelTree._Fields>, java.io.Serializable, Cloneable, Comparable<LevelTree> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("LevelTree");

  private static final org.apache.thrift.protocol.TField FOUR1_FIELD_DESC = new org.apache.thrift.protocol.TField("four1", org.apache.thrift.protocol.TType.STRUCT, (short)1);
  private static final org.apache.thrift.protocol.TField FOUR2_FIELD_DESC = new org.apache.thrift.protocol.TField("four2", org.apache.thrift.protocol.TType.STRUCT, (short)2);

  private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new LevelTreeStandardSchemeFactory();
  private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new LevelTreeTupleSchemeFactory();

  private LevelFour four1; // required
  private LevelFour four2; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    FOUR1((short)1, "four1"),
    FOUR2((short)2, "four2");

    private static final java.util.Map<java.lang.String, _Fields> byName = new java.util.HashMap<java.lang.String, _Fields>();

    static {
      for (_Fields field : java.util.EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // FOUR1
          return FOUR1;
        case 2: // FOUR2
          return FOUR2;
        default:
          return null;
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, throwing an exception
     * if it is not found.
     */
    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null) throw new java.lang.IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    public static _Fields findByName(java.lang.String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final java.lang.String _fieldName;

    _Fields(short thriftId, java.lang.String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public java.lang.String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  public static final java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new java.util.EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.FOUR1, new org.apache.thrift.meta_data.FieldMetaData("four1", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, LevelFour.class)));
    tmpMap.put(_Fields.FOUR2, new org.apache.thrift.meta_data.FieldMetaData("four2", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, LevelFour.class)));
    metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(LevelTree.class, metaDataMap);
  }

  public LevelTree() {
  }

  public LevelTree(
    LevelFour four1,
    LevelFour four2)
  {
    this();
    this.four1 = four1;
    this.four2 = four2;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public LevelTree(LevelTree other) {
    if (other.isSetFour1()) {
      this.four1 = new LevelFour(other.four1);
    }
    if (other.isSetFour2()) {
      this.four2 = new LevelFour(other.four2);
    }
  }

  public LevelTree deepCopy() {
    return new LevelTree(this);
  }

  @Override
  public void clear() {
    this.four1 = null;
    this.four2 = null;
  }

  public LevelFour getFour1() {
    return this.four1;
  }

  public LevelTree setFour1(LevelFour four1) {
    this.four1 = four1;
    return this;
  }

  public void unsetFour1() {
    this.four1 = null;
  }

  /** Returns true if field four1 is set (has been assigned a value) and false otherwise */
  public boolean isSetFour1() {
    return this.four1 != null;
  }

  public void setFour1IsSet(boolean value) {
    if (!value) {
      this.four1 = null;
    }
  }

  public LevelFour getFour2() {
    return this.four2;
  }

  public LevelTree setFour2(LevelFour four2) {
    this.four2 = four2;
    return this;
  }

  public void unsetFour2() {
    this.four2 = null;
  }

  /** Returns true if field four2 is set (has been assigned a value) and false otherwise */
  public boolean isSetFour2() {
    return this.four2 != null;
  }

  public void setFour2IsSet(boolean value) {
    if (!value) {
      this.four2 = null;
    }
  }

  public void setFieldValue(_Fields field, java.lang.Object value) {
    switch (field) {
    case FOUR1:
      if (value == null) {
        unsetFour1();
      } else {
        setFour1((LevelFour)value);
      }
      break;

    case FOUR2:
      if (value == null) {
        unsetFour2();
      } else {
        setFour2((LevelFour)value);
      }
      break;

    }
  }

  public java.lang.Object getFieldValue(_Fields field) {
    switch (field) {
    case FOUR1:
      return getFour1();

    case FOUR2:
      return getFour2();

    }
    throw new java.lang.IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new java.lang.IllegalArgumentException();
    }

    switch (field) {
    case FOUR1:
      return isSetFour1();
    case FOUR2:
      return isSetFour2();
    }
    throw new java.lang.IllegalStateException();
  }

  @Override
  public boolean equals(java.lang.Object that) {
    if (that == null)
      return false;
    if (that instanceof LevelTree)
      return this.equals((LevelTree)that);
    return false;
  }

  public boolean equals(LevelTree that) {
    if (that == null)
      return false;
    if (this == that)
      return true;

    boolean this_present_four1 = true && this.isSetFour1();
    boolean that_present_four1 = true && that.isSetFour1();
    if (this_present_four1 || that_present_four1) {
      if (!(this_present_four1 && that_present_four1))
        return false;
      if (!this.four1.equals(that.four1))
        return false;
    }

    boolean this_present_four2 = true && this.isSetFour2();
    boolean that_present_four2 = true && that.isSetFour2();
    if (this_present_four2 || that_present_four2) {
      if (!(this_present_four2 && that_present_four2))
        return false;
      if (!this.four2.equals(that.four2))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int hashCode = 1;

    hashCode = hashCode * 8191 + ((isSetFour1()) ? 131071 : 524287);
    if (isSetFour1())
      hashCode = hashCode * 8191 + four1.hashCode();

    hashCode = hashCode * 8191 + ((isSetFour2()) ? 131071 : 524287);
    if (isSetFour2())
      hashCode = hashCode * 8191 + four2.hashCode();

    return hashCode;
  }

  @Override
  public int compareTo(LevelTree other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = java.lang.Boolean.valueOf(isSetFour1()).compareTo(other.isSetFour1());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetFour1()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.four1, other.four1);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSetFour2()).compareTo(other.isSetFour2());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetFour2()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.four2, other.four2);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    scheme(iprot).read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    scheme(oprot).write(oprot, this);
  }

  @Override
  public java.lang.String toString() {
    java.lang.StringBuilder sb = new java.lang.StringBuilder("LevelTree(");
    boolean first = true;

    sb.append("four1:");
    if (this.four1 == null) {
      sb.append("null");
    } else {
      sb.append(this.four1);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("four2:");
    if (this.four2 == null) {
      sb.append("null");
    } else {
      sb.append(this.four2);
    }
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    if (four1 == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'four1' was not present! Struct: " + toString());
    }
    if (four2 == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'four2' was not present! Struct: " + toString());
    }
    // check for sub-struct validity
    if (four1 != null) {
      four1.validate();
    }
    if (four2 != null) {
      four2.validate();
    }
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    try {
      write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, java.lang.ClassNotFoundException {
    try {
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class LevelTreeStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public LevelTreeStandardScheme getScheme() {
      return new LevelTreeStandardScheme();
    }
  }

  private static class LevelTreeStandardScheme extends org.apache.thrift.scheme.StandardScheme<LevelTree> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, LevelTree struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // FOUR1
            if (schemeField.type == org.apache.thrift.protocol.TType.STRUCT) {
              struct.four1 = new LevelFour();
              struct.four1.read(iprot);
              struct.setFour1IsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // FOUR2
            if (schemeField.type == org.apache.thrift.protocol.TType.STRUCT) {
              struct.four2 = new LevelFour();
              struct.four2.read(iprot);
              struct.setFour2IsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          default:
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
        }
        iprot.readFieldEnd();
      }
      iprot.readStructEnd();

      // check for required fields of primitive type, which can't be checked in the validate method
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, LevelTree struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.four1 != null) {
        oprot.writeFieldBegin(FOUR1_FIELD_DESC);
        struct.four1.write(oprot);
        oprot.writeFieldEnd();
      }
      if (struct.four2 != null) {
        oprot.writeFieldBegin(FOUR2_FIELD_DESC);
        struct.four2.write(oprot);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class LevelTreeTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public LevelTreeTupleScheme getScheme() {
      return new LevelTreeTupleScheme();
    }
  }

  private static class LevelTreeTupleScheme extends org.apache.thrift.scheme.TupleScheme<LevelTree> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, LevelTree struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      struct.four1.write(oprot);
      struct.four2.write(oprot);
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, LevelTree struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      struct.four1 = new LevelFour();
      struct.four1.read(iprot);
      struct.setFour1IsSet(true);
      struct.four2 = new LevelFour();
      struct.four2.read(iprot);
      struct.setFour2IsSet(true);
    }
  }

  private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
    return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
  }
}
