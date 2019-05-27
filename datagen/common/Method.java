package common;

public class Method {
    private String name;
    private String returnType;
    private String params;
    private String code;
    private String javaDoc;
    
    
    @Override
	public String toString() {
		return "Method [name=" + name + ", returnType=" + returnType + ", params=" + params + ", code=" + code
				+ ", comment=" + javaDoc + "]";
	}


	public String getCode() {
		return code;
	}


	public void setCode(String code) {
		this.code = code;
	}


	public String getJavaDoc() {
		return javaDoc;
	}


	public void setJavaDoc(String javaDoc) {
		this.javaDoc = javaDoc;
	}


	public Method() {
    	this.setName("");
    	this.setReturnType("");
    	this.setParams("");
    }
    

	public Method(String concatenatedMethodInfo) {
    	String splitLines[] = concatenatedMethodInfo.split("\\|");
    	this.setName(splitLines[0]);
    	this.setReturnType("");
    	this.setParams("");
    	if (splitLines.length > 1) {
        	this.returnType = splitLines[1];
        	
        	this.params = (splitLines.length < 3) ? "" : splitLines[2];
    	}
    }
    
    public Method(String name, String returnType, String params) {
       	this.name = name;
    	this.returnType = returnType;
    	this.params = params;
    }
    

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the returnType
	 */
	public String getReturnType() {
		return returnType;
	}

	/**
	 * @param returnType the returnType to set
	 */
	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}

	/**
	 * @return the argTypes
	 */
	public String getParams() {
		return params;
	}

	/**
	 * @param params the argTypes to set
	 */
	public void setParams(String params) {
		this.params = params;
	}

	
	public String getConcatenatedString() {
		return name + "|" + returnType + "|" + params;
	}
	
	public boolean equals(Object obj) {
		Method targetMethod = (Method) obj;
		return (this.getName().equals(targetMethod.getName()) &&
				this.getReturnType().equals(targetMethod.getReturnType()) &&
				this.getParams().equals(targetMethod.getParams()));
	}
}